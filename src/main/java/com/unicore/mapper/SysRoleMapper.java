package com.unicore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicore.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<SysRole> selectRolesByUserId(@Param("userId") Integer userId);
}
