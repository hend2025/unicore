package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicore.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class SysDict extends BaseEntity {
    @TableId
    private String dicId;

    private String dicName;
    private String dicValue;
    private String dicTypeCode;
    private String dicTypeName;
    private Integer orderNum;
    private Integer sysId;
    private String remarks;
}
