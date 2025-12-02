package com.aajumaharjan.demofeatures.auth.service;

import com.aajumaharjan.demofeatures.auth.config.TokenProvider;
import com.aajumaharjan.demofeatures.auth.dto.AuthResponse;
import com.aajumaharjan.demofeatures.auth.dto.UserLoginDto;
import com.aajumaharjan.demofeatures.auth.model.Role;
import com.aajumaharjan.demofeatures.auth.model.UserEntity;
import com.aajumaharjan.demofeatures.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }


    public ResponseEntity<?> generateToken(UserLoginDto req) {

        if (req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }

        final UserEntity userEntity = userRepository.findByEmail(req.getEmail()).orElse(null);

        if (userEntity == null) {
            return ResponseEntity.status(401).build();
        }

        final Set<Role> roles = userEntity.getRoles();

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = tokenProvider.generateToken(authentication);

        var roleNames = roles.stream()
                .map(role -> "ROLE_" + role.getName().replace(" ", "_").toUpperCase())
                .toList();
        var authResponse = new AuthResponse(token, roleNames);

        return ResponseEntity.ok(authResponse);
    }
}
