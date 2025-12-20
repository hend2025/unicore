package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysNotice;

/**
 * 通知公告服务接口
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 分页查询通知公告
     */
    Page<SysNotice> selectPage(Page<SysNotice> page, String noticeTitle, String noticeType);

    /**
     * 新增通知公告
     */
    boolean addNotice(SysNotice notice);

    /**
     * 更新通知公告
     */
    boolean updateNotice(SysNotice notice);

    /**
     * 删除通知公告
     */
    boolean deleteNotice(Integer id);
}
