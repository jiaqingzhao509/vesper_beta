package com.example.common.utils;

import cn.hutool.json.JSONUtil;
import com.example.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

/**
 * Servlet工具类
 *
 * @author tycoding
 *
 */
public class ServletUtil {

    @SneakyThrows
    public static void write(HttpServletResponse response, R data) {
        response.setStatus(data.getCode());
        response.setHeader("Content-type", "application/json;charset=" + CommonConstant.UTF_8);
        response.setCharacterEncoding(CommonConstant.UTF_8);
        response.getWriter().write(JSONUtil.toJsonStr(data));
    }

    @SneakyThrows
    public static void write(HttpServletResponse response, int status, R data) {
        response.setStatus(status);
        response.setHeader("Content-type", "application/json;charset=" + CommonConstant.UTF_8);
        response.setCharacterEncoding(CommonConstant.UTF_8);
        response.getWriter().write(JSONUtil.toJsonStr(data));
    }
}
