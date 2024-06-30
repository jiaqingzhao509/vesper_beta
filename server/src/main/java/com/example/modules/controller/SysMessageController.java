package com.example.modules.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.utils.MybatisUtil;
import com.example.common.utils.QueryPage;
import com.example.common.utils.R;
import com.example.modules.entity.SysMessage;
import com.example.modules.service.SysMessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 */
@RequestMapping("/api/message")
@RestController
@AllArgsConstructor
public class SysMessageController {

    private final SysMessageService sysMessageService;

    @GetMapping("/messages")
    public R getMessages() {
        List<SysMessage> messages = sysMessageService.getMessages();
        return R.ok(messages);
    }

    @GetMapping("/page")
    public R list(SysMessage data, QueryPage queryPage) {
        LambdaQueryWrapper<SysMessage> queryWrapper = Wrappers.<SysMessage>lambdaQuery()
                .like(!StrUtil.isBlank(data.getMessage()), SysMessage::getMessage, data.getMessage())
                .like(!StrUtil.isBlank(data.getUsername()), SysMessage::getUsername, data.getUsername())
                .eq(!StrUtil.isBlank(data.getRole()), SysMessage::getRole, data.getRole())
                .orderByDesc(SysMessage::getCreateTime);
        IPage<SysMessage> iPage = sysMessageService.page(MybatisUtil.wrap(data, queryPage), queryWrapper);
        return R.ok(MybatisUtil.getData(iPage));
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable String id) {
        return R.ok(sysMessageService.getById(id));
    }

    @DeleteMapping("/{id}")
    public R del(@PathVariable String id) {
        return R.ok(sysMessageService.removeById(id));
    }

}
