package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysNotice;
import com.unicore.mapper.SysNoticeMapper;
import com.unicore.service.SysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 通知公告服务实现类
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Override
    public Page<SysNotice> selectPage(Page<SysNotice> page, String noticeTitle, String noticeType) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(noticeTitle)) {
            wrapper.like(SysNotice::getNoticeTitle, noticeTitle);
        }
        if (StringUtils.hasText(noticeType)) {
            wrapper.eq(SysNotice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(SysNotice::getCrteTime);
        return page(page, wrapper);
    }

    @Override
    public boolean addNotice(SysNotice notice) {
        return save(notice);
    }

    @Override
    public boolean updateNotice(SysNotice notice) {
        return updateById(notice);
    }

    @Override
    public boolean deleteNotice(Integer id) {
        return removeById(id);
    }
}
