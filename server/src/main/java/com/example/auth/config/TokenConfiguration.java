package com.example.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author tycoding
 */
@Configuration
public class TokenConfiguration {

    @Bean
    @Primary
    public SaTokenConfig getTokenConfig() {
        return new SaTokenConfig()
                .setIsPrint(false)
                .setTokenName("Authorization")
                .setTimeout(24 * 60 * 60)
                .setTokenStyle("uuid")
                .setIsLog(false)
                .setIsReadCookie(false)
                ;
    }
}
