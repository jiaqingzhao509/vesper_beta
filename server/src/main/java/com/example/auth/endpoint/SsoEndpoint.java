package com.example.auth.endpoint;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.constant.CommonConstant;
import com.example.common.props.AuthProps;
import com.example.common.utils.R;
import com.example.modules.entity.SysUser;
import com.example.modules.service.SysUserService;
import com.xkcoding.http.config.HttpConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/5/31
 */
@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/api/auth")
public class SsoEndpoint {

    private final SysUserService userService;
    private final AuthProps props;

    @ResponseBody
    @RequestMapping("/oauth/google/authorize")
    public R googleAuthorize() {
        AuthRequest authRequest = getAuthRequest();
        return R.ok(Dict.create().set("url", authRequest.authorize(AuthStateUtils.createState())));
    }

    @RequestMapping("/oauth/google/callback")
    public Object googleCallback(AuthCallback callback) {
        AuthResponse res = getAuthRequest().login(callback);
        SysUser user = BeanUtil.copyProperties(res.getData(), SysUser.class);
        JSONObject obj = JSON.parseObject(JSON.toJSONString(res.getData()));
        if (obj == null) {
            return "redirect:" + props.getWebUrl() + "/login?error=Google account login time out please log in again";
        }
        if (obj.get("uuid") != null) {
            // google login
            user.setAppId(obj.get("uuid").toString());
        }

        // check user is exists
        List<SysUser> list = userService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getAppId, user.getAppId()));
        if (!list.isEmpty()) {
            // is exists, do login
            SysUser existsUser = list.get(0);
            user.setId(existsUser.getId());
            user.setChatLimit(existsUser.getChatLimit());
        } else {
            // new user
            user.setChatLimit(props.getLimit());
            userService.save(user);
        }

        SaTokenInfo tokenInfo = onLogin(user);
        return "redirect:" + props.getWebUrl() + "/sso?token=" + tokenInfo.getTokenValue();
    }

    private SaTokenInfo onLogin(SysUser userInfo) {
        StpUtil.login(userInfo.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        userInfo.setPassword(null);
        StpUtil.getSession().set(CommonConstant.AUTH_USER_INFO_KEY, userInfo);
        log.info("====> 登录成功，token={}", tokenInfo.getTokenValue());
        return tokenInfo;
    }

    private AuthRequest getAuthRequest() {
        return new AuthGoogleRequest(AuthConfig.builder().clientId(props.getGoogleKey())
                .clientSecret(props.getGoogleSecret())
                .redirectUri(props.getRedirectUrl() + "/api/auth/oauth/google/callback")
                .httpConfig(HttpConfig.builder().timeout(15000).build()).build());
    }
}
