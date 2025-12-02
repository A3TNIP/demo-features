package com.aajumaharjan.demofeatures.auth.controller;

import com.aajumaharjan.demofeatures.auth.dto.RegisterDto;
import com.aajumaharjan.demofeatures.auth.dto.UserLoginDto;
import com.aajumaharjan.demofeatures.auth.service.AuthService;
import com.aajumaharjan.demofeatures.auth.service.UserService;
import com.aajumaharjan.demofeatures.auth.model.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
        return authService.generateToken(dto);
    }

    @PostMapping("register")
    public UserEntity register(@RequestBody RegisterDto registerDto) {
        return userService.register(registerDto);
    }
}
