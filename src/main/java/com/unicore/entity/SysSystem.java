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
@TableName("sys_system")
public class SysSystem extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer sysId;

    private String sysName;
    private String sysAbbr;
    private Integer prntId;
    private String sysLogo;
    private Integer orderNum;
    private String sysUrl;
    private String homeUrl;

    @TableField(exist = false)
    private List<SysSystem> children;
}
