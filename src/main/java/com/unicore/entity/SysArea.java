package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unicore.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_area")
public class SysArea extends BaseEntity {
    @TableId
    private String areaCode;

    private String areaName;
    private String prntAreaCode;
    private String areaLv;
    private String latlnt;

    @TableField(exist = false)
    private List<SysArea> children;
}
