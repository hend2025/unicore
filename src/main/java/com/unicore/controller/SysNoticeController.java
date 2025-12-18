package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysNotice;
import com.unicore.mapper.SysNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeMapper noticeMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysNotice>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String noticeTitle,
            @RequestParam(required = false) String noticeType) {
        Page<SysNotice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (noticeTitle != null) {
            wrapper.like(SysNotice::getNoticeTitle, noticeTitle);
        }
        if (noticeType != null) {
            wrapper.eq(SysNotice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(SysNotice::getCrteTime);
        return WrapperResponse.success(noticeMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysNotice> getById(@PathVariable Integer id) {
        return WrapperResponse.success(noticeMapper.selectById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysNotice notice) {
        return WrapperResponse.success(noticeMapper.insert(notice) > 0);
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysNotice notice) {
        return WrapperResponse.success(noticeMapper.updateById(notice) > 0);
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        return WrapperResponse.success(noticeMapper.deleteById(id) > 0);
    }
}
