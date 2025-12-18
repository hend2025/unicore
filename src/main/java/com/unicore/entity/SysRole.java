package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer roleId;

    private String roleName;
    private String roleKey;
    private String roleType;
    private String dataScope;
    private Integer sysId;
    private Integer orgId;
    private String remarks;

    @TableField(exist = false)
    private List<String> menuIds;
}
