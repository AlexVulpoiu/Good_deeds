package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.RegisterUserDto;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import com.softbinator_labs.project.good_deeds.dtos.UserInfoDto;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, KeycloakAdminService keycloakAdminService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.encoder = encoder;
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
    public TokenDto registerUser(RegisterUserDto registerUserDto, String userRole) {

        if(userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new BadRequestException("User with username " + registerUserDto.getUsername() + " already exists!");
        }

        if(userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new BadRequestException("User with email " + registerUserDto.getEmail() + " already exists!");
        }

        if(userRepository.existsByPhone(registerUserDto.getPhone())) {
            throw new BadRequestException("User with phone " + registerUserDto.getPhone() + " already exists!");
        }

        User newUser = User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .password(encoder.encode(registerUserDto.getPassword()))
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .phone(registerUserDto.getPhone())
                .points(0)
                .build();

        Long userId = userRepository.save(newUser).getId();

        return keycloakAdminService.addUserToKeycloak(userId, registerUserDto.getPassword(), userRole);
    }
}
