package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysSystem;
import com.unicore.mapper.SysSystemMapper;
import com.unicore.service.SysSystemService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysSystemServiceImpl extends ServiceImpl<SysSystemMapper, SysSystem> implements SysSystemService {

    @Override
    public Page<SysSystem> selectSystemPage(Page<SysSystem> page, SysSystem system) {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        if (system.getSysName() != null) {
            wrapper.like(SysSystem::getSysName, system.getSysName());
        }
        if (system.getStasFlag() != null) {
            wrapper.eq(SysSystem::getStasFlag, system.getStasFlag());
        }
        wrapper.orderByAsc(SysSystem::getOrderNum);
        return page(page, wrapper);
    }

    @Override
    public List<SysSystem> selectSystemTree() {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        wrapper.orderByAsc(SysSystem::getOrderNum);
        List<SysSystem> systems = list(wrapper);
        return buildTree(systems, 0);
    }

    private List<SysSystem> buildTree(List<SysSystem> systems, Integer parentId) {
        return systems.stream()
            .filter(s -> parentId.equals(s.getPrntId()))
            .peek(s -> s.setChildren(buildTree(systems, s.getSysId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<SysSystem> selectSystemList() {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        wrapper.eq(SysSystem::getStasFlag, "1");
        wrapper.orderByAsc(SysSystem::getOrderNum);
        return list(wrapper);
    }
}
