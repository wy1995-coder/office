package com.wy.office.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.office.bean.Permission;
import com.wy.office.bean.PermissionExample;
import com.wy.office.bean.RolePermissionExample;
import com.wy.office.dao.PermissionMapper;
import com.wy.office.dao.RolePermissionMapper;
import com.wy.office.service.PermissionService;
import com.wy.office.utils.BaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 通过员工id返回权限树
     *
     * @param eid 员工id
     * @return
     */
    @Override
    public JSONArray getPermissionsByeid(Integer eid) {
        JSONArray jsonArray = new JSONArray();
        List<Permission> permissionList = permissionMapper.getPermissionsByeid(eid);
        for (Permission permission : permissionList) {
            JSONObject jsonObject = new JSONObject();
            // 一级菜单
            if (permission.getParentid() == 0) {
                jsonObject.put("id", permission.getPid());
                jsonObject.put("text", permission.getPname());
                jsonObject.put("state", "closed");
                JSONArray children = new JSONArray();
                // 二级菜单
                for (Permission per : permissionList) {
                    if (per.getParentid() == permission.getPid()) {
                        JSONObject json = new JSONObject();
                        json.put("id", per.getPid());
                        json.put("text", per.getPname());
                        json.put("state", "open");
                        json.put("url", per.getPurl());
                        children.add(json);
                    }
                }
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 通过员工id返回权限列表
     *
     * @param eid 员工id
     * @return
     */
    @Override
    public List<Permission> getPermsListByeid(Integer eid) {
        return permissionMapper.getPermissionsByeid(eid);
    }


    /**
     * 权限列表
     *
     * @param permission 权限实例，用于搜索参数传递
     * @param pageNumber 当前页码
     * @param pageSize   每页显示条数
     * @return
     */
    @Override
    public JSONObject getPermissionList(Permission permission, int pageNumber, int pageSize) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(pageNumber, pageSize);
        List<Permission> permissionList = permissionMapper.getPermissionList(permission);
        PageInfo<Permission> info = new PageInfo<>(permissionList);
        jsonObject.put("total", info.getTotal());
        jsonObject.put("rows", permissionList);
        return jsonObject;
    }

    /**
     * 通过等级获取权限列表
     *
     * @param plevel
     * @return
     */
    @Override
    public List<Permission> getPermissionByLevel(int plevel) {
        PermissionExample example = new PermissionExample();
        PermissionExample.Criteria criteria = example.createCriteria();
        criteria.andPlevelEqualTo(plevel);
        return permissionMapper.selectByExample(example);
    }

    /**
     * 添加权限
     *
     * @param permission 权限
     * @return
     */
    @Override
    public BaseResult addPermission(Permission permission, Integer parentId) {
        BaseResult reuslt = new BaseResult();
        log.info("addPermission方法执行，入参为：permission={}, parentId={}", permission, parentId);
        try {
            // 判断是否权限code是否唯一
            PermissionExample example = new PermissionExample();
            PermissionExample.Criteria criteria = example.createCriteria();
            criteria.andPcodeEqualTo(permission.getPcode());
            List<Permission> permissionList = permissionMapper.selectByExample(example);
            if (permissionList != null && permissionList.size() > 0) {
                reuslt.setSuccess(false);
                reuslt.setMessage("功能代码重复");
                return reuslt;
            }
            // 一级功能
            if (parentId == null) {
                permission.setParentid(0);
                permission.setRemark1("一级功能");
                // 二级功能
            } else {
                permission.setParentid(parentId);
                permission.setRemark1(permissionMapper.selectByPrimaryKey(parentId).getPname());
            }
            permissionMapper.insertSelective(permission);
            reuslt.setSuccess(true);
            reuslt.setMessage("添加成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            reuslt.setSuccess(false);
            reuslt.setMessage("操作失败");
        }
        // 添加权限
        log.info("addPermission方法结束，出参为{}", reuslt);
        return reuslt;
    }

    /**
     * 通过权限id获取权限
     *
     * @param pid 权限id
     * @return
     */
    @Override
    public Permission getPermissionByPid(int pid) {
        return permissionMapper.selectByPrimaryKey(pid);
    }

    /**
     * 修改权限
     *
     * @param permission 权限对象
     * @return
     */
    @Override
    @Transactional
    public BaseResult updatePermission(Permission permission, Integer parentId) {
        log.info("进入修改权限方法，入参为permission={},parentId={}", permission, parentId);
        BaseResult result = new BaseResult();
        try {
            PermissionExample example = new PermissionExample();
            PermissionExample.Criteria criteria = example.createCriteria();
            criteria.andPcodeEqualTo(permission.getPcode());
            criteria.andPidNotEqualTo(permission.getPid());
            List<Permission> permissionList = permissionMapper.selectByExample(example);
            // 判断是否重复
            if (permissionList != null && permissionList.size() > 0) {
                result.setMessage("权限代码重复");
                result.setSuccess(false);
                return result;
            }
            // 变更为二级功能，删除原一级菜单上下面的二级菜单
            if (permission.getPlevel() == 2) {
                PermissionExample example1 = new PermissionExample();
                PermissionExample.Criteria criteria1 = example1.createCriteria();
                criteria1.andParentidEqualTo(permission.getPid());
                permissionMapper.deleteByExample(example1);
                Permission parentPer = permissionMapper.selectByPrimaryKey(parentId);
                // 设置父功能名称
                permission.setRemark1(parentPer.getPname());
                // 设置父功能父id
                permission.setParentid(parentId);
            } else {
                permission.setParentid(0);
                permission.setRemark1("一级功能");
            }
            permissionMapper.updateByPrimaryKey(permission);
            result.setMessage("操作成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("操作失败");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return result;
        }
        log.info("修改权限方法执行结束，出参为{}", result);
        return result;
    }

    /**
     * 删除权限
     *
     * @param pid
     * @return
     */
    @Override
    @Transactional
    public BaseResult deletePermissionByPid(int pid) {
        BaseResult result = new BaseResult();
        log.info("进入删除权限方法，入参为{}", pid);
        try {
            // 获取要删除的权限
            PermissionExample example = new PermissionExample();
            PermissionExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(pid);
            List<Permission> permissionList = permissionMapper.selectByExample(example);
            // 判断是一级还是二级功能,一级删除所有的二级菜单
            if (permissionList != null && permissionList.size() > 0) {
                Permission permission = permissionList.get(0);
                if (permission.getPlevel() == 1) {
                    // 获取所有子功能再删除
                    List<Permission> subPermission = permissionMapper.getSubPermissionByParentid(permission.getPid());
                    for (Permission per : subPermission) {
                        // 删除子功能关联表的记录
                        RolePermissionExample example1 = new RolePermissionExample();
                        RolePermissionExample.Criteria criteria1 = example1.createCriteria();
                        criteria1.andPidEqualTo(per.getPid());
                        rolePermissionMapper.deleteByExample(example1);
                        // 删除子功能
                        permissionMapper.deleteByPrimaryKey(per.getPid());
                    }
                }
                // 删除关联
                RolePermissionExample example1 = new RolePermissionExample();
                RolePermissionExample.Criteria criteria1 = example1.createCriteria();
                criteria1.andPidEqualTo(permission.getPid());
                rolePermissionMapper.deleteByExample(example1);
                // 删除功能
                permissionMapper.deleteByPrimaryKey(permission.getPid());
            }
            result.setMessage("删除成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("删除权限方法，出参为{}", result);
        return result;
    }

    /**
     * 批量删除
     *
     * @param pids
     * @return
     */
    @Override
    @Transactional
    public BaseResult batchDeletePermissionBypids(String pids) {
        BaseResult result = new BaseResult();
        log.info("进入批量删除方法，入参为{}", pids);
        try {
            String[] pidArray = pids.substring(0, pids.lastIndexOf(",")).split(",");
            for (String temp : pidArray) {
                BaseResult tempResult = deletePermissionByPid(Integer.parseInt(temp));
                if (!tempResult.isSuccess()) {
                    throw new Exception();
                }
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("操作失败");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("批量删除方法结束，出参为{}", result);
        return result;
    }


    /**
     * 权限列表
     * @param parentid 父id
     * @return
     */
    @Override
    public List<Permission> getPermissisonByParentId(Integer parentid) {
        return permissionMapper.getPermissisonByParentId(parentid);
    }

    /**
     * 权限列表和角色的权限
     * @param rid
     * @return
     */
    @Override
    public JSONArray getRolePermList(int rid) {
        List<Permission> perList = permissionMapper.selectRoleCheckedPer(rid);
        JSONArray jsonArray = new JSONArray();
        for (Permission per : perList) {
            // 一级菜单
            if (per.getParentid() == 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", per.getPid());
                jsonObject.put("text", per.getPname());
                jsonObject.put("state", "open");
                JSONArray children = new JSONArray();
                // 二级菜单
                for (Permission child : perList) {
                    if (child.getParentid() == per.getPid()) {
                        JSONObject json = new JSONObject();
                        json.put("id", child.getPid());
                        json.put("text", child.getPname());
                        json.put("state", "open");
                        json.put("url", child.getPurl());
                        if (child.getChecked() == 1) {
                            json.put("checked", "checked");
                        }
                        children.add(json);
                    }
                }
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 角色详情页面的权限列表
     *
     * @param rid
     * @return
     */
    @Override
    public JSONArray getRoleDetailPer(int rid) {
        List<Permission> perList = permissionMapper.getRoleDetailPer(rid);
        JSONArray jsonArray = new JSONArray();
        for (Permission per : perList) {
            // 一级菜单
            if (per.getParentid() == 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", per.getPid());
                jsonObject.put("text", per.getPname());
                jsonObject.put("state", "open");
                JSONArray children = new JSONArray();
                // 二级菜单
                for (Permission child : perList) {
                    if (child.getParentid() == per.getPid()) {
                        JSONObject json = new JSONObject();
                        json.put("id", child.getPid());
                        json.put("text", child.getPname());
                        json.put("state", "open");
                        json.put("url", child.getPurl());
                        children.add(json);
                    }
                }
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
