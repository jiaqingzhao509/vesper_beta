package com.example.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.modules.entity.SysMessage;

import java.util.List;

/**
 * @author tycoding
 */
public interface SysMessageService extends IService<SysMessage> {

    SysMessage addMessage(SysMessage message);

    void clearMessage();

    List<SysMessage> getMessages();

}

