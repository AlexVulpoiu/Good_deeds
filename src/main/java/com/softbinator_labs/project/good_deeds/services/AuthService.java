package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.clients.AuthClient;
import com.softbinator_labs.project.good_deeds.dtos.LoginDto;
import com.softbinator_labs.project.good_deeds.dtos.RefreshTokenDto;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthClient authClient;

    private final UserRepository userRepository;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Autowired
    public AuthService(AuthClient authClient, UserRepository userRepository) {
        this.authClient = authClient;
        this.userRepository = userRepository;
    }

    @SneakyThrows
    public TokenDto login(LoginDto loginDto) {
        Optional<User> inAppUser = userRepository.findByEmail(loginDto.getEmail());
        if(inAppUser.isEmpty()) {
            throw new NotFoundException("The user doesn't exist!");
        }

        MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();
        loginCredentials.add("client_id", keycloakClient);
        loginCredentials.add("username", inAppUser.get().getId().toString());
        loginCredentials.add("password", loginDto.getPassword());
        loginCredentials.add("grant_type", loginDto.getGrantType());

        return authClient.login(loginCredentials);
    }

    @SneakyThrows
    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {

        MultiValueMap<String, String> refreshCredentials = new LinkedMultiValueMap<>();
        refreshCredentials.add("client_id", keycloakClient);
        refreshCredentials.add("refresh_token", refreshTokenDto.getRefreshToken());
        refreshCredentials.add("grant_type", refreshTokenDto.getGrantType());

        return authClient.refresh(refreshCredentials);
    }
}
