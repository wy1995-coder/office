package com.wy.office.controller;

import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Role;
import com.wy.office.service.RoleService;
import com.wy.office.utils.BaseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 角色列表
     * @param role 角色查询条件
     * @param page 当前页
     * @param rows 每页展示数据条数
     */
    @ResponseBody
    @RequestMapping("roleList")
    public JSONObject getRoleList(Role role, int page, int rows) {
        return roleService.getRoleList(role, page, rows);
    }

    /**
     * 添加角色
     * @param role 角色对象
     */
    @ResponseBody
    @RequestMapping("addRole")
    public BaseResult addRole(Role role) {
        return roleService.addRole(role);
    }

    /**
     * 将要修改的角色对象保存在session中
     * @param rid 角色id
     */
    @ResponseBody
    @RequestMapping("updateRoleByRid")
    public BaseResult updateRoleByRid(int rid) {
        Session session = SecurityUtils.getSubject().getSession();
        Role role = roleService.getRoleByRid(rid);
        session.setAttribute("updateRole", role);
        BaseResult result = new BaseResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * 从session获取要修改的角色对象
     */
    @ResponseBody
    @RequestMapping("getRoleFromSession")
    public Role getRoleFromSession() {
        Session session = SecurityUtils.getSubject().getSession();
        return (Role) session.getAttribute("updateRole");
    }

    /**
     * 修改角色
     * @param role 角色对象
     */
    @ResponseBody
    @RequestMapping("updateRole")
    public BaseResult updateRole(Role role) {
        return roleService.updateRole(role);
    }

    /**
     * 删除角色
     * @param rid 角色id
     */
    @ResponseBody
    @RequestMapping("deleteRoleByrid")
    public BaseResult deleteRoleByrid(int rid) {
        return roleService.deleteRoleByrid(rid);
    }

    /**
     * 批量删除角色
     * @param rids 角色id字符串
     */
    @ResponseBody
    @RequestMapping("batchDeleteRole")
    public BaseResult batchDeleteRole(String rids) {
        return roleService.batchDeleteRole(rids);
    }

    /**
     * 获取添加员工页面的角色列表
     */
    @ResponseBody
    @RequestMapping("getRoleList4AddEmployee")
    public List<Role> getRoleList4AddEmployee() {
        return roleService.getRoleList4AddEmployee();
    }

}
