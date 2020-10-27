package com.wy.office.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Employee;
import com.wy.office.bean.Permission;
import com.wy.office.service.PermissionService;
import com.wy.office.utils.BaseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取员工权限菜单
     */
    @ResponseBody
    @RequestMapping("menuPermission")
    public JSONArray getPermissions() {
        Session session = SecurityUtils.getSubject().getSession();
        Employee employee = (Employee) session.getAttribute("employee");
        return permissionService.getPermissionsByeid(employee.getEid());
    }

    /**
     * 获取权限列表
     * @param permission 查询条件
     * @param page 当前页
     * @param rows 每页展示条数
     */
    @ResponseBody
    @RequestMapping("permissionList")
    public JSONObject getAllPermission(Permission permission, int page, int rows) {
        return permissionService.getPermissionList(permission, page, rows);
    }

    /**
     * 通过等级获取权限列表
     * @param plevel 权限等级
     */
    @RequestMapping("getPermissionByLevel")
    @ResponseBody
    public List<Permission> getPermissionByLevel(int plevel) {
        return permissionService.getPermissionByLevel(plevel);
    }

    /**
     * 添加权限
     * @param permission 权限对象
     * @param parentId 父id
     */
    @ResponseBody
    @RequestMapping("addPermission")
    public BaseResult addPermission(Permission permission, @RequestParam("parentId") Integer parentId) {
        return permissionService.addPermission(permission, parentId);
    }

    /**
     * 获取要更新的权限保存到session
     * @param pid 权限id
     */
    @ResponseBody
    @RequestMapping("updatePermissionByPid")
    public BaseResult updatePermissionByPid(int pid) {
        Permission permission = permissionService.getPermissionByPid(pid);
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("updatePermission", permission);
        BaseResult result = new BaseResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * 从session中获取要修改的权限
     */
    @ResponseBody
    @RequestMapping("getUpdatePermission")
    public Permission getUpdatePermission() {
        Session session = SecurityUtils.getSubject().getSession();
        return (Permission) session.getAttribute("updatePermission");
    }

    /**
     * 修改权限
     * @param permission 权限
     * @param parentId 父id
     */
    @ResponseBody
    @RequestMapping("updatePermission")
    public BaseResult updatePermission(Permission permission, @RequestParam("parentId") Integer parentId) {
        return permissionService.updatePermission(permission, parentId);
    }

    /**
     * 删除权限
     * @param pid 权限id
     */
    @ResponseBody
    @RequestMapping("deletePermissionByPid")
    public BaseResult deletePermissionByPid(int pid) {
        return permissionService.deletePermissionByPid(pid);
    }

    /**
     * 批量删除权限
     * @param pids 权限id
     */
    @ResponseBody
    @RequestMapping("batchDeletePermission")
    public BaseResult batchDeletePermission(String pids) {
        return permissionService.batchDeletePermissionBypids(pids);
    }

    /**
     * 通过父id获取权限
     * @param parentid 父id
     */
    @ResponseBody
    @RequestMapping("getPermissisonByParentId")
    public List<Permission> getPermissisonByParentId(Integer parentid) {
        return permissionService.getPermissisonByParentId(parentid);
    }


    /**
     * 获取权限列表和角色对应的权限
     */
    @ResponseBody
    @RequestMapping("getRolePermList")
    public JSONArray getRolePermList(int rid) {
        return permissionService.getRolePermList(rid);
    }

    /**
     * 角色详情页面的权限列表
     * @param rid
     * @return
     */
    @ResponseBody
    @RequestMapping("getRoleDetailPer")
    public JSONArray getRoleDetailPer(int rid) {
        return permissionService.getRoleDetailPer(rid);
    }

}