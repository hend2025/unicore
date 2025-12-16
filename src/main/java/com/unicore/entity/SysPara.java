package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicore.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_para")
public class SysPara extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer paraId;

    private String paraCode;
    private String paraName;
    private String paraValue;
    private String paraType;
    private Integer sysId;
    private String paraDesc;
}
