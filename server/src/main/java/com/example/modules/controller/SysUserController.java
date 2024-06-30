package com.example.modules.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.utils.MybatisUtil;
import com.example.common.utils.QueryPage;
import com.example.common.utils.R;
import com.example.modules.entity.SysUser;
import com.example.modules.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/checkName")
    public R<Boolean> checkName(SysUser sysUser) {
        return R.ok(userService.checkName(sysUser));
    }

    @GetMapping("/list")
    public R<List<SysUser>> list(SysUser data) {
        return R.ok(userService.list(Wrappers.lambdaQuery()));
    }

    @GetMapping("/page")
    public R<Dict> page(SysUser user, QueryPage queryPage) {
        return R.ok(MybatisUtil.getData(userService.page(user, queryPage)));
    }

    @GetMapping("/{id}")
    public R<SysUser> findById(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @PostMapping
    public R<SysUser> add(@RequestBody SysUser data) {
        userService.save(data);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            userService.removeById(id);
        }
        return R.ok();
    }
}
