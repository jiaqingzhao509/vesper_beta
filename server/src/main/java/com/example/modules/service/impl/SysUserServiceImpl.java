package com.example.modules.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.QueryPage;
import com.example.modules.entity.SysUser;
import com.example.modules.mapper.SysUserMapper;
import com.example.modules.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tycoding
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByName(String username) {
        List<SysUser> list = list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Boolean checkName(SysUser data) {
        List<SysUser> list = baseMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, data.getUsername()));
        return list.isEmpty();
    }

    @Override
    public IPage<SysUser> page(SysUser data, QueryPage queryPage) {
        IPage<SysUser> page = baseMapper.selectPage(new Page<>(queryPage.getPage(), queryPage.getLimit()), Wrappers.lambdaQuery());

        IPage<SysUser> iPage = new Page<>();
        if (!page.getRecords().isEmpty()) {
            List<SysUser> list = BeanUtil.copyToList(page.getRecords(), SysUser.class);
            iPage.setRecords(list)
                    .setCurrent(page.getCurrent())
                    .setPages(page.getPages())
                    .setSize(page.getSize())
                    .setTotal(page.getTotal());
        }

        return iPage;
    }
}
