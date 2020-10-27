package com.wy.office.service;

import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Employee;
import com.wy.office.utils.BaseResult;
import com.wy.office.utils.EmployeeResult;

import java.io.IOException;

public interface EmployeeService {

    /**
     * 登录
     * @param jobnumber 工号
     * @param password 密码
     * @return
     */
    EmployeeResult login(String jobnumber, String password);

    /**
     * shiro登录
     * @return
     */
    EmployeeResult shiroLogin(String jobnumber, String password);

    /**
     * 查询员工列表
     * @param employee 查询条件
     * @param page 当前页
     * @param rows 每页条数
     * @return
     */
    JSONObject getEmployeeList(Employee employee, int page, int rows);

    /**
     * 获取当前登录用户
     * @return
     */
    Employee getCurrentLoginUser();

    /**
     * 添加员工
     * @param employee
     * @return
     */
    BaseResult addEmployee(Employee employee);

    /**
     * 通过eid获取员工
     * @param eid
     * @return
     */
    Employee getEmployeeAndRolesByEid(int eid);

    /**
     * 修改员工
     * @param employee
     * @return
     */
    BaseResult updateEmployee(Employee employee);

    /**
     * 删除员工
     * @param eid
     * @return
     */
    BaseResult deleteEmployee(int eid);

    /**
     * 批量删除员工
     * @param eids
     * @return
     */
    BaseResult batchDeleteEmployee(String eids);

    /**
     * 导出员工信息
     * @param eids 员工id字符串，为空则全部导出
     * @return
     */
    byte[] exportContent(String eids);
}