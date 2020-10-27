package com.wy.office.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.office.bean.*;
import com.wy.office.dao.EmployeeRoleMapper;
import com.wy.office.dao.RoleMapper;
import com.wy.office.dao.RolePermissionMapper;
import com.wy.office.service.RoleService;
import com.wy.office.utils.BaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private static Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private EmployeeRoleMapper employeeRoleMapper;

    /**
     * 获取角色列表
     *
     * @param role 角色对象，封装查询条件
     * @param page 当前页
     * @param rows 每页显示数据数
     * @return
     */
    @Override
    public JSONObject getRoleList(Role role, int page, int rows) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, rows);
        List<Role> roleList = roleMapper.getRoleList(role);
        PageInfo<Role> info = new PageInfo<>(roleList);
        jsonObject.put("total", info.getTotal());
        jsonObject.put("rows", roleList);
        return jsonObject;
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @Override
    @Transactional
    public BaseResult addRole(Role role) {
        BaseResult result = new BaseResult();
        log.info("进入添加角色方法，入参为{}", role);
        try {
            // 判断角色编码唯一
            RoleExample example = new RoleExample();
            RoleExample.Criteria criteria = example.createCriteria();
            criteria.andRcodeEqualTo(role.getRcode());
            List<Role> roleList = roleMapper.selectByExample(example);
            if (roleList != null && roleList.size() > 0) {
                result.setMessage("角色编码重复，添加失败");
                result.setSuccess(false);
                return result;
            }
            // 添加角色
            roleMapper.insert(role);
            // 循环添加角色-权限功能
            String[] pids = role.getPids().split(",");
            for (String pid : pids) {
                RolePermission rp = new RolePermission();
                rp.setRid(role.getRid());
                rp.setPid(Integer.parseInt(pid));
                rolePermissionMapper.insert(rp);
            }
            result.setMessage("添加成功");
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setMessage("操作失败");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("添加角色方法结束，出参为{}", result);
        return result;
    }

    /**
     * 查找要修改的角色信息保存在session
     *
     * @param rid
     * @return
     */
    @Override
    public Role getRoleByRid(int rid) {
        return roleMapper.selectByPrimaryKey(rid);
    }

    /**
     * 修改角色信息
     * @param role 角色
     * @return
     */
    @Override
    @Transactional
    public BaseResult updateRole(Role role) {
        BaseResult result = new BaseResult();
        log.info("进入修改角色信息方法，入参为{}", role);
        try {
            // 判断角色编码唯一
            RoleExample roleExample = new RoleExample();
            RoleExample.Criteria roleExampleCriteria = roleExample.createCriteria();
            roleExampleCriteria.andRcodeEqualTo(role.getRcode());
            roleExampleCriteria.andRidNotEqualTo(role.getRid());
            List<Role> roleList = roleMapper.selectByExample(roleExample);
            if (roleList != null && roleList.size() > 0) {
                result.setMessage("角色编码重复，修改失败");
                result.setSuccess(false);
                return result;
            }
            // 修改信息
            roleMapper.updateByPrimaryKey(role);
            // 删除角色-权限的关联
            RolePermissionExample example = new RolePermissionExample();
            RolePermissionExample.Criteria criteria = example.createCriteria();
            criteria.andRidEqualTo(role.getRid());
            rolePermissionMapper.deleteByExample(example);
            // 重新添加角色-权限的关联
            String[] pidArray = role.getPids().substring(0, role.getPids().lastIndexOf(",")).split(",");
            for (String pid : pidArray) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRid(role.getRid());
                rolePermission.setPid(Integer.valueOf(pid));
                rolePermissionMapper.insert(rolePermission);
            }
            result.setMessage("操作成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("修改角色方法结束，出参为{}", result);
        return result;
    }


    /**
     * 删除角色
     * @param rid
     * @return
     */
    @Override
    @Transactional
    public BaseResult deleteRoleByrid(int rid) {
        BaseResult result = new BaseResult();
        log.info("进入删除角色方法，入参为{}", rid);
        try {
            // 删除角色
            roleMapper.deleteByPrimaryKey(rid);
            // 删除角色-权限关联
            RolePermissionExample example = new RolePermissionExample();
            RolePermissionExample.Criteria criteria = example.createCriteria();
            criteria.andRidEqualTo(rid);
            rolePermissionMapper.deleteByExample(example);
            // 删除角色-用户关联
            EmployeeRoleExample example1 = new EmployeeRoleExample();
            EmployeeRoleExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andRidEqualTo(rid);
            employeeRoleMapper.deleteByExample(example1);
            result.setMessage("操作成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("删除角色方法结束，出参为{}",result);
        return result;
    }

    /**
     * 批量删除角色
     * @param rids 角色id字符串
     * @return
     */
    @Override
    @Transactional
    public BaseResult batchDeleteRole(String rids) {
        BaseResult result = new BaseResult();
        log.info("进入批量删除角色方法，入参为{}", rids);
        try {
            rids = rids.substring(0, rids.lastIndexOf(","));
            String[] ridArray = rids.split(",");
            for (String rid : ridArray) {
                BaseResult result1 = deleteRoleByrid(Integer.valueOf(rid));
                if (!result1.isSuccess()) {
                    throw new Exception();
                }
            }
            result.setMessage("操作成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("批量删除角色方法结束，出参为{}", result);
        return result;
    }

    /**
     * 在添加员工页面获取角色列表
     * @return
     */
    @Override
    public List<Role> getRoleList4AddEmployee() {
        RoleExample example = new RoleExample();
        return roleMapper.selectByExample(example);
    }
}
