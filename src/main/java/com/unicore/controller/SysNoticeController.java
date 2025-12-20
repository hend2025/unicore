package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysNotice;
import com.unicore.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService noticeService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysNotice>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String noticeTitle,
            @RequestParam(required = false) String noticeType) {
        Page<SysNotice> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(noticeService.selectPage(page, noticeTitle, noticeType));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysNotice> getById(@PathVariable Integer id) {
        return WrapperResponse.success(noticeService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysNotice notice) {
        return WrapperResponse.success(noticeService.addNotice(notice));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysNotice notice) {
        return WrapperResponse.success(noticeService.updateNotice(notice));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        return WrapperResponse.success(noticeService.deleteNotice(id));
    }
}
