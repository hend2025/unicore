package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysArea;
import com.unicore.mapper.SysAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sys/area")
public class SysAreaController {

    @Autowired
    private SysAreaMapper areaMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysArea>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String areaName) {
        Page<SysArea> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysArea::getValiFlag, "1");
        if (areaName != null) {
            wrapper.like(SysArea::getAreaName, areaName);
        }
        return WrapperResponse.success(areaMapper.selectPage(page, wrapper));
    }

    @GetMapping("/tree")
    public WrapperResponse<List<SysArea>> tree() {
        LambdaQueryWrapper<SysArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysArea::getValiFlag, "1");
        List<SysArea> list = areaMapper.selectList(wrapper);
        return WrapperResponse.success(buildTree(list, null));
    }

    @GetMapping("/{code}")
    public WrapperResponse<SysArea> getByCode(@PathVariable String code) {
        return WrapperResponse.success(areaMapper.selectById(code));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysArea area) {
        return WrapperResponse.success(areaMapper.insert(area) > 0);
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysArea area) {
        return WrapperResponse.success(areaMapper.updateById(area) > 0);
    }

    @DeleteMapping("/{code}")
    public WrapperResponse<Boolean> delete(@PathVariable String code) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(areaMapper.deleteById(code) > 0);
    }

    private List<SysArea> buildTree(List<SysArea> list, String parentCode) {
        // 收集所有节点的编码
        java.util.Set<String> allCodes = list.stream()
            .map(SysArea::getAreaCode)
            .collect(Collectors.toSet());
        
        return list.stream()
            .filter(a -> {
                String prnt = a.getPrntAreaCode();
                if (parentCode == null) {
                    // 根节点：父级编码为空或父级编码不在当前列表中
                    return prnt == null || prnt.isEmpty() || !allCodes.contains(prnt);
                }
                return parentCode.equals(prnt);
            })
            .peek(a -> a.setChildren(buildTree(list, a.getAreaCode())))
            .collect(Collectors.toList());
    }
}
