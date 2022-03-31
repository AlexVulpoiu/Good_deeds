package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.RegisterUserDto;
import com.softbinator_labs.project.good_deeds.services.UserService;
import com.softbinator_labs.project.good_deeds.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getDetails(Authentication authentication) {
        return new ResponseEntity<>(KeycloakHelper.getUser(authentication), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return new ResponseEntity<>(userService.registerUser(registerUserDto, "ROLE_USER"), HttpStatus.OK);
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return new ResponseEntity<>(userService.registerUser(registerUserDto, "ROLE_ADMIN"), HttpStatus.OK);
    }
}
