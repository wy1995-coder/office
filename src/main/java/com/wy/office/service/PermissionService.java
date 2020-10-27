package com.wy.office.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Permission;
import com.wy.office.utils.BaseResult;

import java.util.List;

public interface PermissionService {

    /**
     * 获取员工对应的权限功能菜单
     *
     * @param eid 员工id
     * @return
     */
    JSONArray getPermissionsByeid(Integer eid);

    /**
     * 获取员工对应的权限
     *
     * @param eid
     * @return
     */
    List<Permission> getPermsListByeid(Integer eid);

    /**
     * 权限列表
     *
     * @return
     */
    JSONObject getPermissionList(Permission permission, int pageNumber, int pageSize);

    /**
     * 通过等级获取权限列表
     *
     * @param plevel
     * @return
     */
    List<Permission> getPermissionByLevel(int plevel);

    /**
     * 添加权限
     *
     * @param permission
     * @return
     */
    BaseResult addPermission(Permission permission, Integer parentId);

    /**
     * 通过pid获取权限
     *
     * @param pid 权限id
     * @return
     */
    Permission getPermissionByPid(int pid);

    /**
     * 修改权限
     *
     * @param permission 权限对象
     * @return
     */
    BaseResult updatePermission(Permission permission, Integer parentId);

    /**
     * 删除权限
     * @param pid
     * @return
     */
    BaseResult deletePermissionByPid(int pid);

    /**
     * 批量删除权限
     * @param pids
     * @return
     */
    BaseResult batchDeletePermissionBypids(String pids);

    /**
     * 获取权限列表
     * @param plevel 权限级别
     * @param parent 父id
     * @return
     */
    List<Permission> getPermissisonByParentId(Integer parentid);

    /**
     * 获取权限列表和角色对应的权限
     * @param rid
     * @return
     */
    JSONArray getRolePermList(int rid);

    /**
     * 角色详情页面的权限列表
     * @param rid
     * @return
     */
    JSONArray getRoleDetailPer(int rid);
}
