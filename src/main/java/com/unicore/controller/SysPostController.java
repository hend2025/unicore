package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysPost;
import com.unicore.mapper.SysPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/post")
public class SysPostController {

    @Autowired
    private SysPostMapper postMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysPost>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String postName) {
        Page<SysPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getValiFlag, "1");
        if (postName != null) {
            wrapper.like(SysPost::getPostName, postName);
        }
        wrapper.orderByAsc(SysPost::getOrderNum);
        return WrapperResponse.success(postMapper.selectPage(page, wrapper));
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysPost>> list() {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getValiFlag, "1");
        wrapper.orderByAsc(SysPost::getOrderNum);
        return WrapperResponse.success(postMapper.selectList(wrapper));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysPost> getById(@PathVariable Integer id) {
        return WrapperResponse.success(postMapper.selectById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysPost post) {
        return WrapperResponse.success(postMapper.insert(post) > 0);
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysPost post) {
        return WrapperResponse.success(postMapper.updateById(post) > 0);
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(postMapper.deleteById(id) > 0);
    }
}
