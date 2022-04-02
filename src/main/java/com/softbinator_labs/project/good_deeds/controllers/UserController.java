package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.RegisterUserDto;
import com.softbinator_labs.project.good_deeds.services.UserService;
import com.softbinator_labs.project.good_deeds.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private static final String SITE_URL = "http://localhost:8088";

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
        return new ResponseEntity<>(userService.registerUser(registerUserDto, "ROLE_USER", SITE_URL), HttpStatus.OK);
    }

    @PostMapping("/processRegister")
    public String processRegisterUser(RegisterUserDto registerUserDto, HttpServletRequest request) {
        userService.registerUser(registerUserDto, "ROLE_USER", getSiteURL(request));
        return "registerSuccess";
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return new ResponseEntity<>(userService.registerUser(registerUserDto, "ROLE_ADMIN", SITE_URL), HttpStatus.OK);
    }

    @PostMapping("/processRegisterAdmin")
    public String processRegisterAdmin(RegisterUserDto registerUserDto, HttpServletRequest request) {
        userService.registerUser(registerUserDto, "ROLE_ADMIN", getSiteURL(request));
        return "registerSuccess";
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return "verifySuccess";
        } else {
            return "verifyFail";
        }
    }

    @GetMapping("/myVouchers")
    public ResponseEntity<?> getMyVouchers() {
        return userService.getMyVouchers();
    }

    @PostMapping("/purchaseVoucher/{voucherId}")
    public ResponseEntity<?> purchaseVoucher(@PathVariable Long voucherId) {
        return userService.purchaseVoucher(voucherId);
    }
}
