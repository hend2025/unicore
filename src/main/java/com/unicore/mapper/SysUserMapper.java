package com.unicore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicore.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<String> selectPermsByUserId(@Param("userId") Integer userId);
    List<SysUser> selectUserList(@Param("user") SysUser user);
}
