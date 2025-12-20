package com.unicore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unicore.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<String> selectPermsByUserId(@Param("userId") Integer userId);

    IPage<SysUser> selectUserPage(IPage<SysUser> page, @Param("user") SysUser user);

    @Deprecated
    List<SysUser> selectUserList(@Param("user") SysUser user);
}
