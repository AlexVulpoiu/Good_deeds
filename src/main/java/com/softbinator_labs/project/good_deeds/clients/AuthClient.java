package com.softbinator_labs.project.good_deeds.clients;

import com.softbinator_labs.project.good_deeds.config.AuthClientConfig;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(value = "auth", configuration = AuthClientConfig.class, url = "${my.keycloak.url}")
public interface AuthClient {

    @PostMapping("${my.keycloak.auth.endpoint}")
    TokenDto login(Map<String, ?> loginForm);

    @PostMapping("${my.keycloak.auth.endpoint}")
    TokenDto refresh(Map<String, ?> refreshForm);
}
