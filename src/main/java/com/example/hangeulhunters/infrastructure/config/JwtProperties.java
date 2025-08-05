package com.example.hangeulhunters.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
}

