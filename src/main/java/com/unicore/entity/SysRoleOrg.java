package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_org")
public class SysRoleOrg {
    @TableId(type = IdType.AUTO)
    private Integer roleOrgId;

    private Integer roleId;
    private Integer orgId;
}
