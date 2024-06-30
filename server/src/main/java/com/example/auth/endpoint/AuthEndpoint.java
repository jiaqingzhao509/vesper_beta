package com.example.auth.endpoint;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.example.auth.common.OpencageData;
import com.example.auth.config.AuthUtil;
import com.example.auth.service.TokenInfo;
import com.example.common.constant.CommonConstant;
import com.example.common.props.AuthProps;
import com.example.common.utils.R;
import com.example.modules.entity.SysUser;
import com.example.modules.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tycoding
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthEndpoint {

    private final SysUserService userService;
    private final AuthProps props;

    @GetMapping("/info")
    public R<SysUser> info() {
        return R.ok(AuthUtil.getUserInfo());
    }

    @PostMapping("/login")
    public R login(@RequestBody SysUser user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new RuntimeException("The user name or password is empty");
        }
        SysUser userInfo = userService.getByName(user.getUsername());
        if (userInfo == null) {
            throw new RuntimeException("The user name or password is incorrect");
        }

        String decryptPass = AuthUtil.getDecryptPass(userInfo.getPassword());
        if (!decryptPass.equals(user.getPassword())) {
            throw new RuntimeException("The user name or password is incorrect");
        }

        SaTokenInfo tokenInfo = onLogin(userInfo);

        return R.ok(new TokenInfo().setToken(tokenInfo.tokenValue).setExpiration(tokenInfo.tokenTimeout));
    }

    @PostMapping("/register")
    public R register(@RequestBody SysUser user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new RuntimeException("The user name or password is empty");
        }
        SysUser userInfo = userService.getByName(user.getUsername());
        if (userInfo != null) {
            throw new RuntimeException("The account is already registered");
        }

        String decryptPass = AuthUtil.getEncodePass(user.getPassword());
        user.setPassword(decryptPass);
        user.setCreateTime(new Date());
        user.setChatLimit(props.getLimit());
        userService.save(user);

        SaTokenInfo tokenInfo = onLogin(user);
        return R.ok(new TokenInfo().setToken(tokenInfo.tokenValue)
                .setExpiration(tokenInfo.tokenTimeout)
        );
    }

    private void getChart(SysUser userInfo) {
        if (userInfo.getBirthdate() == null) {
            throw new RuntimeException("birthdate is null");
        }
        String date = DateUtil.format(userInfo.getBirthdate(), "yyyy-MM-dd");
        String time = DateUtil.format(userInfo.getBirthdate(), "HH:mm");
        Dict data = Dict.create()
                .set("date", date)
                .set("time", time)
                .set("lat", userInfo.getLat())
                .set("lng", userInfo.getLng());

        try {
            String res = HttpRequest.post(props.getChartUrl())
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(data))
                    .timeout(30 * 1000)
                    .execute()
                    .body();
            log.info("获取星盘信息: {}", res);
            userInfo.setChart(res);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to obtain astrolabe information. Please contact the administrator");
        }
    }

    private SaTokenInfo onLogin(SysUser userInfo) {
        getChart(userInfo);

        StpUtil.login(userInfo.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        userInfo.setPassword(null);
        StpUtil.getSession().set(CommonConstant.AUTH_USER_INFO_KEY, userInfo);
        log.info("====> 登录成功，token={}", tokenInfo.getTokenValue());
        return tokenInfo;
    }

    @ResponseBody
    @DeleteMapping("/logout")
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/checkPlace")
    public R checkPlace(@RequestParam String place) {
        Map<String, Object> params = Map.of("q", place, "key", "1e660713cb444c40ae916eaa55feaf58");
        String res = HttpUtil.get("https://api.opencagedata.com/geocode/v1/json", params);
        OpencageData data = JSON.parseObject(res, OpencageData.class);
        List<OpencageData.OpencageItem> results = data.getResults();
        if (results != null && !results.isEmpty()) {
            OpencageData.OpencageItem item = results.get(0);
            return R.ok(Dict.create()
                    .set("lat", item.getGeometry().getLat())
                    .set("lng", item.getGeometry().getLng())
                    .set("location", item.getFormatted())
            );
        }
        return R.ok();
    }

    @PutMapping("/update")
    public R update(@RequestBody SysUser data) {
        if (StrUtil.isBlank(data.getPassword())) {
            data.setPassword(null);
        } else {
            String decryptPass = AuthUtil.getEncodePass(data.getPassword());
            data.setPassword(decryptPass);
        }

        getChart(data);
        StpUtil.getSession().set(CommonConstant.AUTH_USER_INFO_KEY, data);

        userService.updateById(data);
        return R.ok();
    }

}
