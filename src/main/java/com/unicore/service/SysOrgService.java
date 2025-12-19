package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysOrg;
import java.util.List;

public interface SysOrgService extends IService<SysOrg> {
    Page<SysOrg> selectOrgPage(Page<SysOrg> page, SysOrg org);
    List<SysOrg> selectOrgTree();
    List<SysOrg> selectOrgList(SysOrg org);
    
    /**
     * 删除机构（校验是否被用户引用）
     */
    boolean deleteOrg(Integer orgId);
}
