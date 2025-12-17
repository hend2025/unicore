package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.unicore.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer userId;

    private String userName;
    private String realName;
    private String certNo;
    private String certType;
    private Integer orgId;
    private String admdvsCode;
    private String userType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private String salt;

    private String avatar;
    private String gend;
    private String mobile;
    private String email;
    private String areaCode;
    private String addr;
    private String loginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    private String remarks;
    private Integer crteId;
    private String crteName;

    @TableField(exist = false)
    private List<Integer> roleIds;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private String admdvsName;
}
