package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysArea;
import com.unicore.entity.SysOrg;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysAreaMapper;
import com.unicore.mapper.SysOrgMapper;
import com.unicore.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sys/area")
public class SysAreaController {

    @Autowired
    private SysAreaMapper areaMapper;

    @Autowired
    private SysOrgMapper orgMapper;

    @Autowired
    private SysUserMapper userMapper;

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

    @PostMapping("/update")
    public WrapperResponse<Boolean> update(@RequestBody SysArea area) {
        return WrapperResponse.success(areaMapper.updateById(area) > 0);
    }

    @GetMapping("/delete/{code}")
    public WrapperResponse<Boolean> delete(@PathVariable String code) {
        // 校验地区是否被机构引用
        LambdaQueryWrapper<SysOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(SysOrg::getAreaCode, code);
        orgWrapper.eq(SysOrg::getValiFlag, "1");
        if (orgMapper.selectCount(orgWrapper) > 0) {
            throw new RuntimeException("该地区已被机构使用，无法删除");
        }
        // 校验地区是否被用户引用
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getAreaCode, code);
        userWrapper.eq(SysUser::getValiFlag, "1");
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new RuntimeException("该地区已被用户使用，无法删除");
        }
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
