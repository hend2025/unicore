package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysAdmdvs;
import com.unicore.entity.SysOrg;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysAdmdvsMapper;
import com.unicore.mapper.SysOrgMapper;
import com.unicore.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sys/admdvs")
public class SysAdmdvsController {

    @Autowired
    private SysAdmdvsMapper admdvsMapper;

    @Autowired
    private SysOrgMapper orgMapper;

    @Autowired
    private SysUserMapper userMapper;

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
        // 校验医保区划是否被机构引用
        LambdaQueryWrapper<SysOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(SysOrg::getAdmdvsCode, code);
        orgWrapper.eq(SysOrg::getValiFlag, "1");
        if (orgMapper.selectCount(orgWrapper) > 0) {
            throw new RuntimeException("该医保区划已被机构使用，无法删除");
        }
        // 校验医保区划是否被用户引用
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getAdmdvsCode, code);
        userWrapper.eq(SysUser::getValiFlag, "1");
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new RuntimeException("该医保区划已被用户使用，无法删除");
        }
        return WrapperResponse.success(admdvsMapper.deleteById(code) > 0);
    }

    private List<SysAdmdvs> buildTree(List<SysAdmdvs> list, String parentCode) {
        // 收集所有节点的编码
        java.util.Set<String> allCodes = list.stream()
                .map(SysAdmdvs::getAdmdvsCode)
                .collect(Collectors.toSet());

        return list.stream()
                .filter(a -> {
                    String prnt = a.getPrntAdmdvsCode();
                    if (parentCode == null) {
                        // 根节点：父级编码为空或父级编码不在当前列表中
                        return prnt == null || prnt.isEmpty() || !allCodes.contains(prnt);
                    }
                    return parentCode.equals(prnt);
                })
                .peek(a -> a.setChildren(buildTree(list, a.getAdmdvsCode())))
                .collect(Collectors.toList());
    }
}
