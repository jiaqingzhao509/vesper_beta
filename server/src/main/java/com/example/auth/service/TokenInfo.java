package com.example.auth.service;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tycoding
 */
@Data
@Accessors(chain = true)
public class TokenInfo<T> {

    /**
     * Token
     */
    private String token;

    /**
     * 过期时间
     */
    private Long expiration;

    /**
     * 用户信息
     */
    private T user;
}
