package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysSystem;
import java.util.List;

public interface SysSystemService extends IService<SysSystem> {
    Page<SysSystem> selectSystemPage(Page<SysSystem> page, SysSystem system);
    List<SysSystem> selectSystemTree();
}
