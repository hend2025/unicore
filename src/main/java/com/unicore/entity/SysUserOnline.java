package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_user_online")
public class SysUserOnline {
    @TableId
    private String sessionId;

    private String loginName;
    private String deptName;
    private String ipAddr;
    private String loginLoc;
    private String browser;
    private String os;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAccTime;

    private Integer expireTime;
}
