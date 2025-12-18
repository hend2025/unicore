package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysOrg;
import com.unicore.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/org")
public class SysOrgController {

    @Autowired
    private SysOrgService orgService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysOrg>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysOrg org) {
        Page<SysOrg> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(orgService.selectOrgPage(page, org));
    }

    @GetMapping("/tree")
    public WrapperResponse<List<SysOrg>> tree() {
        return WrapperResponse.success(orgService.selectOrgTree());
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysOrg>> list(SysOrg org) {
        return WrapperResponse.success(orgService.selectOrgList(org));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysOrg> getById(@PathVariable Integer id) {
        return WrapperResponse.success(orgService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysOrg org) {
        return WrapperResponse.success(orgService.save(org));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysOrg org) {
        return WrapperResponse.success(orgService.updateById(org));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(orgService.removeById(id));
    }
}
