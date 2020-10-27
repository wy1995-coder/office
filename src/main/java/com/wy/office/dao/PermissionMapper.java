package com.wy.office.dao;

import com.wy.office.bean.Permission;
import com.wy.office.bean.PermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PermissionMapper {
    long countByExample(PermissionExample example);

    int deleteByExample(PermissionExample example);

    int deleteByPrimaryKey(Integer pid);

    int insert(Permission record);

    int insertSelective(Permission record);

    List<Permission> selectByExample(PermissionExample example);

    Permission selectByPrimaryKey(Integer pid);

    int updateByExampleSelective(@Param("record") Permission record, @Param("example") PermissionExample example);

    int updateByExample(@Param("record") Permission record, @Param("example") PermissionExample example);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);

    /**
     * 通过员工id获取权限列表
     * @param eid
     * @return
     */
    List<Permission> getPermissionsByeid(Integer eid);

    /**
     * 权限列表
     * @param permission
     * @return
     */
    List<Permission> getPermissionList(Permission permission);

    /**
     * 获取一级菜单的所有子菜单
     * @param pid
     * @return
     */
    List<Permission> getSubPermissionByParentid(Integer pid);

    /**
     * 权限列表 (添加角色用)
     * @param plevel
     * @param parentid
     * @return
     */
    List<Permission> getPermissisonByParentId(Integer parentid);

    /**
     * 权限列表和角色对应权限
     * @param rid
     * @return
     */
    List<Permission> selectRoleCheckedPer(int rid);

    /**
     * 角色详情页面的权限列表
     * @param rid
     * @return
     */
    List<Permission> getRoleDetailPer(int rid);
}