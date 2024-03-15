package com.homfo.user.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserJwtConfig {
    @Bean
    public List<String> userAccessTokenWhiteList() {
        return List.of("/users/register", "/users/sign-in");
    }

    @Bean
    public List<String> userRefreshTokenBlackList() {
        return List.of("/users/refresh");
    }
}
