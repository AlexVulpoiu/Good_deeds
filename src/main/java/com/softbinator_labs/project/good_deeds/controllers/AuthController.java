package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.LoginDto;
import com.softbinator_labs.project.good_deeds.dtos.RefreshTokenDto;
import com.softbinator_labs.project.good_deeds.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(RefreshTokenDto refreshTokenDto) {
        return new ResponseEntity<>(authService.refresh(refreshTokenDto), HttpStatus.OK);
    }
}
