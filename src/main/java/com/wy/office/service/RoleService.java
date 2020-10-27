package com.wy.office.service;

import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Role;
import com.wy.office.utils.BaseResult;

import java.util.List;

public interface RoleService {

    /**
     * 分页查询角色列表
     * @param role 角色对象，封装查询条件
     * @param page 当前页
     * @param rows 每页显示数据数
     * @return
     */
    JSONObject getRoleList(Role role, int page, int rows);

    /**
     * 添加角色
     * @param role
     * @return
     */
    BaseResult addRole(Role role);

    /**
     * 通过id获取角色
     * @param rid
     * @return
     */
    Role getRoleByRid(int rid);

    /**
     * 修改角色信息
     * @param role
     * @return
     */
    BaseResult updateRole(Role role);

    /**
     * 删除角色信息
     * @param rid
     * @return
     */
    BaseResult deleteRoleByrid(int rid);

    /**
     * 批量删除角色信息
     * @param rids
     * @return
     */
    BaseResult batchDeleteRole(String rids);

    /**
     * 在添加员工页面获取角色列表
     * @return
     */
    List<Role> getRoleList4AddEmployee();
}
