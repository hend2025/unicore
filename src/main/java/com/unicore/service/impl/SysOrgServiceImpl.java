package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysOrg;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysOrgMapper;
import com.unicore.mapper.SysUserMapper;
import com.unicore.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public Page<SysOrg> selectOrgPage(Page<SysOrg> page, SysOrg org) {
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getValiFlag, "1");
        if (org.getOrgName() != null && !org.getOrgName().isEmpty()) {
            wrapper.like(SysOrg::getOrgName, org.getOrgName());
        }
        if (org.getOrgCode() != null && !org.getOrgCode().isEmpty()) {
            wrapper.like(SysOrg::getOrgCode, org.getOrgCode());
        }
        if (org.getStasFlag() != null && !org.getStasFlag().isEmpty()) {
            wrapper.eq(SysOrg::getStasFlag, org.getStasFlag());
        }
        wrapper.orderByAsc(SysOrg::getOrderNum);
        return page(page, wrapper);
    }

    @Override
    public List<SysOrg> selectOrgTree() {
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getValiFlag, "1");
        wrapper.orderByAsc(SysOrg::getOrderNum);
        List<SysOrg> orgs = list(wrapper);
        return buildTree(orgs, 0);
    }

    @Override
    public List<SysOrg> selectOrgList(SysOrg org) {
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getValiFlag, "1");
        if (org.getOrgName() != null) {
            wrapper.like(SysOrg::getOrgName, org.getOrgName());
        }
        if (org.getOrgCode() != null) {
            wrapper.like(SysOrg::getOrgCode, org.getOrgCode());
        }
        if (org.getStasFlag() != null) {
            wrapper.eq(SysOrg::getStasFlag, org.getStasFlag());
        }
        wrapper.orderByAsc(SysOrg::getOrderNum);
        return list(wrapper);
    }

    private List<SysOrg> buildTree(List<SysOrg> orgs, Integer parentId) {
        return orgs.stream()
            .filter(o -> parentId.equals(o.getPrntOrgId()))
            .peek(o -> o.setChildren(buildTree(orgs, o.getOrgId())))
            .collect(Collectors.toList());
    }

    @Override
    public boolean deleteOrg(Integer orgId) {
        // 校验机构是否被用户引用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOrgId, orgId);
        wrapper.eq(SysUser::getValiFlag, "1");
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该机构已被用户使用，无法删除");
        }
        return removeById(orgId);
    }
}
