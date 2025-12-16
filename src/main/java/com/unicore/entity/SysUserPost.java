package com.unicore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_post")
public class SysUserPost {
    @TableId(type = IdType.AUTO)
    private Integer userPostId;

    private Integer userId;
    private Integer postId;
}
