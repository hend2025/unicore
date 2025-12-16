package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_logininfor")
public class SysLogininfor {
    @TableId(type = IdType.AUTO)
    private Long loginId;

    private Integer userId;
    private String userName;
    private String loginIp;
    private String loginLoc;
    private String browser;
    private String os;
    private String loginStat;
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
}
