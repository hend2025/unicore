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
@TableName("sys_admdvs")
public class SysAdmdvs extends BaseEntity {
    @TableId
    private String admdvsCode;

    private String admdvsName;
    private String prntAdmdvsCode;
    private String admdvsLv;
    private String latlnt;

    @TableField(exist = false)
    private List<SysAdmdvs> children;
}
