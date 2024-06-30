package com.example.modules.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.auth.config.AuthUtil;
import com.example.modules.entity.SysMessage;
import com.example.modules.mapper.SysMessageMapper;
import com.example.modules.service.SysMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author tycoding
 */
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    @Override
    public SysMessage addMessage(SysMessage message) {
        message.setCreateTime(new Date());
        baseMapper.insert(message);
        return message;
    }

    @Override
    public void clearMessage() {
        baseMapper.delete(
                Wrappers.<SysMessage>lambdaQuery()
                        .eq(SysMessage::getUserId, AuthUtil.getUserId()));
    }

    @Override
    public List<SysMessage> getMessages() {
        // 避免页面渲染压力大，只截取最新的20条数据
        return baseMapper.selectPage(new Page<>(0, 80), Wrappers.<SysMessage>lambdaQuery()
                .eq(SysMessage::getUserId, AuthUtil.getUserId())
                .orderByDesc(SysMessage::getCreateTime)
        ).getRecords();
    }
}

