package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysOrg;
import com.unicore.mapper.SysOrgMapper;
import com.unicore.service.SysOrgService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

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
}
