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
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    @TableId(type = IdType.INPUT)
    private String menuId;

    private String menuName;
    private String parentId;
    private Integer sysId;
    private Integer orderNum;
    private String menuUrl;
    private String menuComp;
    private String menuQuery;
    private String isFrame;
    private String isCache;
    private String menuType;
    private String menuIcon;
    private String menuPerms;
    private String isShow;

    @TableField(exist = false)
    private List<SysMenu> children;
}
