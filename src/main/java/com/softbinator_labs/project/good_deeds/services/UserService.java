package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.RegisterUserDto;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import com.softbinator_labs.project.good_deeds.dtos.UserInfoDto;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public UserService(UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    public UserInfoDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist!"));

        return UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @SneakyThrows
    public TokenDto registerUser(RegisterUserDto registerUserDto) {

        if(userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new BadRequestException("User with email " + registerUserDto.getEmail() + " already exists!");
        }

        User newUser = User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .build();
        Long userId = userRepository.save(newUser).getId();

        return keycloakAdminService.addUserToKeycloak(userId, registerUserDto.getPassword());
    }
}
