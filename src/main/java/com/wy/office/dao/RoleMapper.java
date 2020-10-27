package com.wy.office.dao;

import com.wy.office.bean.Role;
import com.wy.office.bean.RoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper {
    long countByExample(RoleExample example);

    int deleteByExample(RoleExample example);

    int deleteByPrimaryKey(Integer rid);

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByExample(RoleExample example);

    Role selectByPrimaryKey(Integer rid);

    int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByExample(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> getRoleByeid(Integer eid);

    /**
     * 获取角色信息列表
     * @param role
     * @return
     */
    List<Role> getRoleList(Role role);

    /**
     * 获取角色信息和角色的权限
     * @param rid
     * @return
     */
    Role getRoleAndPermission(int rid);
}