package com.unicore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicore.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> selectMenusByUserId(@Param("userId") Integer userId);
    List<String> selectMenuIdsByRoleId(@Param("roleId") Integer roleId);
    
    /**
     * 查询指定父节点下的最大菜单ID
     * @param sysId 系统ID
     * @param parentId 父节点ID
     * @return 最大菜单ID
     */
    String selectMaxMenuId(@Param("sysId") Integer sysId, @Param("parentId") String parentId);
}
