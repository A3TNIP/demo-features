package com.aajumaharjan.demofeatures.auth.config;


import com.aajumaharjan.demofeatures.auth.AuthProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthProperties properties;
    private final UserDetailsService userDetailsService;
    private final TokenProvider jwtTokenUtil;
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;

    public JwtAuthenticationFilter(AuthProperties properties,
                                   UserDetailsService userDetailsService,
                                   TokenProvider jwtTokenUtil,
                                   UnauthorizedEntryPoint unauthorizedEntryPoint) {
        this.properties = properties;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(properties.getJwt().getHeaderString());
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(properties.getJwt().getTokenPrefix())) {
            authToken = header.replace(properties.getJwt().getTokenPrefix(), "").trim();
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("An error occurred while fetching Username from Token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired", e);
                unauthorizedEntryPoint.commence(req, res, null);
                return;
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("Couldn't find bearer string, header will be ignored");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(authToken, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken,
                            SecurityContextHolder.getContext().getAuthentication(), userDetails);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.error("An error occurred while fetching user details", e);
                res.sendError(SC_UNAUTHORIZED, "Unauthorized");
            }
        }

        chain.doFilter(req, res);
    }
}
