package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_oss")
public class SysOss {
    @TableId(type = IdType.AUTO)
    private Long ossId;

    private String fileName;
    private String origName;
    private String fileSuffix;
    private String fileUrl;
    private String service;
    private Integer crteId;
    private String crteName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date crteTime;
}
