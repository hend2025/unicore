package com.unicore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysOrg;
import java.util.List;

public interface SysOrgService extends IService<SysOrg> {
    List<SysOrg> selectOrgTree();
    List<SysOrg> selectOrgList(SysOrg org);
}
