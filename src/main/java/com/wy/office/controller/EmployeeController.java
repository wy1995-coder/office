package com.wy.office.controller;

import com.alibaba.fastjson.JSONObject;
import com.wy.office.bean.Employee;
import com.wy.office.service.EmployeeService;
import com.wy.office.utils.BaseResult;
import com.wy.office.utils.EmployeeResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param jobnumber 用户名
     * @param password 密码
     */
    @RequestMapping("/login")
    @ResponseBody
    public EmployeeResult login(String jobnumber, String password) {
//        EmployeeResult result = employeeService.login(jobnumber, password);
//        session.setAttribute("employee", result.getEmployee());
        return employeeService.shiroLogin(jobnumber, password);
    }

    /**
     * 查询员工列表
     * @param employee 条件查询的条件封装对象
     * @param page 当前页
     * @param rows 每页显示数据条数
     */
    @ResponseBody
    @RequestMapping("employeeList")
    public JSONObject getEmployeeList(Employee employee, int page, int rows) {
        return employeeService.getEmployeeList(employee, page, rows);
    }

    /**
     * 获取当前登录用户
     * @return 员工对象
     */
    @ResponseBody
    @RequestMapping("currentLoginUser")
    public Employee currentLoginUser() {
        return employeeService.getCurrentLoginUser();
    }

    /**
     * 添加用户
     */
    @ResponseBody
    @RequestMapping("addEmployee")
    public BaseResult addEmployee(Employee employee) {
        return employeeService.addEmployee(employee);
    }

    /**
     * 获取要修改的员工存到session中
     */
    @ResponseBody
    @RequestMapping("updateEmployeeByEid")
    public BaseResult updateEmployeeByEid(int eid) {
        BaseResult result = new BaseResult();
        Employee employee = employeeService.getEmployeeAndRolesByEid(eid);
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("updateEmployee", employee);
        result.setSuccess(true);
        return result;
    }

    /**
     * 从session中获取修改的员工信息
     * @return 员工对象
     */
    @ResponseBody
    @RequestMapping("getEmployeeFromSession")
    public Employee getEmployeeFromSession() {
        Session session = SecurityUtils.getSubject().getSession();
        return (Employee) session.getAttribute("updateEmployee");
    }

    /**
     * 修改员工
     * @param employee 要修改的员工信息
     */
    @ResponseBody
    @RequestMapping("updateEmployee")
    public BaseResult updateEmployee(Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    /**
     * 删除员工信息
     * @param eid 员工id
     */
    @ResponseBody
    @RequestMapping("deleteEmployeeByEid")
    public BaseResult deleteEmployeeByEid(int eid) {
        return employeeService.deleteEmployee(eid);
    }

    /**
     * 批量删除员工
     * @param eids 员工id字符串
     */
    @ResponseBody
    @RequestMapping("batchDeleteEmployee")
    public BaseResult batchDeleteEmployee(String eids) {
        return employeeService.batchDeleteEmployee(eids);
    }

    /**
     * 导出
     * @return excel文件
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export(@RequestParam(value = "eids", required = false) String eids)  {
        String filename = "employeeList.xls";
        byte[] bytes = employeeService.exportContent(eids);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
