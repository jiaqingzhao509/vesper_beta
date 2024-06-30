package com.example.auth.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.example.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author tycoding
 *
 */
@Configuration
@AllArgsConstructor
public class AuthConfiguration {

    private final String[] skipUrl = new String[]{
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/checkPlace/**",
            "/api/auth/oauth/google/authorize",
            "/api/auth/oauth/google/callback",
    };

    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")

                .setAuth(obj -> {
                    SaRouter.match("/**")
                            .notMatch(skipUrl)
                            .check(r -> hasAuth())
                    ;
                })
                .setError(this::handleError);
    }

    private String handleError(Throwable e) {
        // 设置响应头
        SaHolder.getResponse()
                .setStatus(HttpStatus.UNAUTHORIZED.value())
                .setHeader("Content-Type", "application/json;charset=UTF-8");
        return JSON.toJSONString(R.fail(HttpStatus.UNAUTHORIZED));
    }

    private void hasAuth() {
        StpUtil.checkLogin();
    }
}
