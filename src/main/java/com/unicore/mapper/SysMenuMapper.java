package com.unicore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicore.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> selectMenusByUserId(@Param("userId") Integer userId);
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Integer roleId);
}
