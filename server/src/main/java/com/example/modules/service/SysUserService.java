package com.example.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.QueryPage;
import com.example.modules.entity.SysUser;

/**
 * @author tycoding
 */
public interface SysUserService extends IService<SysUser> {

    SysUser getByName(String username);
    /**
     * 校验用户登录信息
     */
    Boolean checkName(SysUser data);

    /**
     * 账户列表
     */
    IPage<SysUser> page(SysUser data, QueryPage queryPage);
}
