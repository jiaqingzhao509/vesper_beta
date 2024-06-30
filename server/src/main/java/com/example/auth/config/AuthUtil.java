package com.example.auth.config;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.example.common.constant.CommonConstant;
import com.example.modules.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 权限相关方法
 *
 * @author tycoding
 * @since 2024/4/15
 */
public class AuthUtil {

    /**
     * 获取Request对象
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 获取Response对象
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

    /**
     * 截取前端Token字符串中不包含`Bearer`的部分
     */
    public static String getToken(String token) {
        if (token != null && token.toLowerCase().startsWith("bearer")) {
            return token.replace("bearer", "").trim();
        }
        return token;
    }

    /**
     * 获取用户数据
     */
    public static SysUser getUserInfo() {
        try {
            return (SysUser) StpUtil.getSession().get(CommonConstant.AUTH_USER_INFO_KEY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        SysUser user = getUserInfo();
        if (user == null) {
            return null;
        }
        return user.getUsername();
    }

    /**
     * 获取登录用户ID
     */
    public static Long getUserId() {
        SysUser userInfo = getUserInfo();
        if (userInfo == null) {
            return null;
        }
        return userInfo.getId();
    }

    /**
     * 密码加密
     */
    public static String encode(String salt, String password) {
        return SaSecureUtil.aesEncrypt(salt, password);
    }

    /**
     * 密码解密
     */
    public static String decrypt(String salt, String password) {
        return SaSecureUtil.aesDecrypt(salt, password);
    }

    public static String getDecryptPass(String password) {
        return decrypt("salt", password);
    }

    public static String getEncodePass(String password) {
        return encode("salt", password);
    }

    public static void main(String[] args) {
        System.out.println(encode("salt", "123456"));
    }
}
