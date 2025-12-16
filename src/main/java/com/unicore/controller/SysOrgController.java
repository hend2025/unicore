package com.unicore.controller;

import com.unicore.common.WrapperResponse;
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
