package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysAdmdvs;
import com.unicore.mapper.SysAdmdvsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sys/admdvs")
public class SysAdmdvsController {

    @Autowired
    private SysAdmdvsMapper admdvsMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysAdmdvs>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String admdvsName) {
        Page<SysAdmdvs> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAdmdvs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmdvs::getValiFlag, "1");
        if (admdvsName != null) {
            wrapper.like(SysAdmdvs::getAdmdvsName, admdvsName);
        }
        return WrapperResponse.success(admdvsMapper.selectPage(page, wrapper));
    }

    @GetMapping("/tree")
    public WrapperResponse<List<SysAdmdvs>> tree() {
        LambdaQueryWrapper<SysAdmdvs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmdvs::getValiFlag, "1");
        List<SysAdmdvs> list = admdvsMapper.selectList(wrapper);
        return WrapperResponse.success(buildTree(list, null));
    }

    @GetMapping("/{code}")
    public WrapperResponse<SysAdmdvs> getByCode(@PathVariable String code) {
        return WrapperResponse.success(admdvsMapper.selectById(code));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysAdmdvs admdvs) {
        return WrapperResponse.success(admdvsMapper.insert(admdvs) > 0);
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysAdmdvs admdvs) {
        return WrapperResponse.success(admdvsMapper.updateById(admdvs) > 0);
    }

    @DeleteMapping("/{code}")
    public WrapperResponse<Boolean> delete(@PathVariable String code) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(admdvsMapper.deleteById(code) > 0);
    }

    private List<SysAdmdvs> buildTree(List<SysAdmdvs> list, String parentCode) {
        return list.stream()
            .filter(a -> parentCode == null ? a.getPrntAdmdvsCode() == null : parentCode.equals(a.getPrntAdmdvsCode()))
            .peek(a -> a.setChildren(buildTree(list, a.getAdmdvsCode())))
            .collect(Collectors.toList());
    }
}
