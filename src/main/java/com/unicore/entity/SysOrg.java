package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicore.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_org")
public class SysOrg extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer orgId;

    private Integer prntOrgId;
    private String orgCode;
    private String orgName;
    private Integer orderNum;
    private String prntOrgName;
    private String orgType;
    private String socCredCode;
    private String admdvsCode;
    private String areaCode;
    private String orgLv;
    private String orgAddr;
    private String latlnt;
    private String legalName;
    private String contName;
    private String contPhone;
    private Integer crteId;
    private String crteName;
    private Integer updtId;
    private String updtName;

    @TableField(exist = false)
    private List<SysOrg> children;
}
