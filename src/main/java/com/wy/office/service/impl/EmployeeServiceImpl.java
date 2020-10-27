package com.wy.office.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wy.office.bean.Employee;
import com.wy.office.bean.EmployeeExample;
import com.wy.office.bean.EmployeeRole;
import com.wy.office.bean.EmployeeRoleExample;
import com.wy.office.dao.EmployeeMapper;
import com.wy.office.dao.EmployeeRoleMapper;
import com.wy.office.service.EmployeeService;
import com.wy.office.utils.BaseResult;
import com.wy.office.utils.DateUtil;
import com.wy.office.utils.EmployeeResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeRoleMapper employeeRoleMapper;

    /**
     * 登录
     * @param jobnumber 工号
     * @param password  密码
     */
    @Override
    public EmployeeResult login(String jobnumber, String password) {
        EmployeeResult result = new EmployeeResult();
        log.info("登录方法开始，入参为：{}", jobnumber);
        try {
            Employee checkUnique = employeeMapper.checkJobNumberUnique(jobnumber);
            result.setSuccess(true);
            if (checkUnique == null) {
                result.setMessage("工号不存在");
                result.setLoginSuccess(false);
                return result;
            }
            Employee employee = employeeMapper.login(jobnumber, password);
            if (employee != null) {
                result.setLoginSuccess(true);
                result.setMessage("登录成功");
                result.setEmployee(employee);
            } else {
                result.setLoginSuccess(false);
                result.setMessage("密码不正确");
            }
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            result.setSuccess(false);
            result.setMessage("操作失败");
        }
        log.info("登录方法结束，出参为：{}", result);
        return result;
    }

    /**
     * shiro登录
     * @param jobnumber 用户名
     * @param password 密码
     */
    @Override
    public EmployeeResult shiroLogin(String jobnumber, String password) {
        EmployeeResult result = new EmployeeResult();
        log.info("shiro登录方法，入参为：{}", jobnumber);
        Subject subject = SecurityUtils.getSubject();
        // 封装用户名和密码到token中
        UsernamePasswordToken token = new UsernamePasswordToken(jobnumber, password);
        try {
            subject.login(token);
            // 登录对象保存到session中
            Session session = subject.getSession();
            Employee employee = (Employee) subject.getPrincipal();
            session.setAttribute("employee", employee);
            result.setSuccess(true);
            result.setLoginSuccess(true);
            result.setMessage("登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("登录失败");
            result.setLoginSuccess(false);
            result.setSuccess(false);
        }
        log.info("shiro登录方法结束，出参为：{}", result);
        return result;
    }

    /**
     * 查询员工列表
     * @param employee 查询条件
     * @param page 当前页
     * @param rows 每页条数
     */
    @Override
    public JSONObject getEmployeeList(Employee employee, int page, int rows) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, rows);
        List<Employee> employeeList = employeeMapper.getEmployeeList(employee);
        for (Employee emp : employeeList) {
            emp.setHireDateStr(DateUtil.DateToString(emp.getHireDate(),"yyyy年MM月dd日"));
        }
        PageInfo<Employee> info = new PageInfo<>(employeeList);
        jsonObject.put("total", info.getTotal());
        jsonObject.put("rows", employeeList);
        return jsonObject;
    }

    /**
     * 获取当前登录用户
     */
    @Override
    public Employee getCurrentLoginUser() {
        Session session = SecurityUtils.getSubject().getSession();
        return (Employee) session.getAttribute("employee");
    }

    /**
     * 添加员工
     * @param employee 添加的员工信息
     */
    @Override
    @Transactional
    public BaseResult addEmployee(Employee employee) {
        log.info("添加员工方法开始，入参为{}", employee);
        BaseResult result = new BaseResult();
        try {
            // 工号唯一
            EmployeeExample example = new EmployeeExample();
            EmployeeExample.Criteria criteria = example.createCriteria();
            criteria.andJobnumberEqualTo(employee.getJobnumber());
            List<Employee> employeeList = employeeMapper.selectByExample(example);
            if (employeeList != null && employeeList.size() > 0) {
                result.setSuccess(false);
                result.setMessage("工号重复，请确认添加信息");
                return result;
            }
            // 获取加密的盐值
            String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
            // 使用md5加密，明文密码，盐值，加密次数
            String encryptedPassword = new Md5Hash(employee.getPassword(), salt, 3).toString();
            employee.setPassword(encryptedPassword);
            // 将盐值保存到数据库，用remark1字段接收
            employee.setRemark1(salt);
            // employee表添加记录，返回employee的主键id
            employeeMapper.insert(employee);
            // employee-role表添加记录
            String rids = employee.getRids();
            String[] ridArray = rids.split(",");
            for (String rid : ridArray) {
                EmployeeRole employeeRole = new EmployeeRole();
                employeeRole.setEid(employee.getEid());
                employeeRole.setRid(Integer.valueOf(rid));
                employeeRoleMapper.insert(employeeRole);
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("添加员工方法结束，出参为{}", result);
        return result;
    }

    /**
     * 通过eid获取员工
     * @param eid 员工id
     */
    @Override
    public Employee getEmployeeAndRolesByEid(int eid) {
        Employee employee = employeeMapper.getEmployeeAndRoles(eid);
        employee.setHireDateStr(DateUtil.DateToString(employee.getHireDate(),"yyyy-MM-dd"));
        return employee;
    }

    /**
     * 修改员工
     * @param employee 要修改的员工信息
     */
    @Override
    @Transactional
    public BaseResult updateEmployee(Employee employee) {
        BaseResult result = new BaseResult();
        log.info("修改员工方法开始，入参为{}",employee);
        try {
            // 工号唯一
            EmployeeExample example = new EmployeeExample();
            EmployeeExample.Criteria criteria = example.createCriteria();
            criteria.andJobnumberEqualTo(employee.getJobnumber());
            criteria.andEidNotEqualTo(employee.getEid());
            List<Employee> employeeList = employeeMapper.selectByExample(example);
            if (employeeList != null && employeeList.size() > 0) {
                result.setSuccess(false);
                result.setMessage("工号重复，请确认填写");
                return result;
            }
            // 更新employee表
            employeeMapper.updateByPrimaryKeySelective(employee);
            // 删除employee-role表记录
            EmployeeRoleExample employeeRoleExample = new EmployeeRoleExample();
            EmployeeRoleExample.Criteria employeeRoleExampleCriteria = employeeRoleExample.createCriteria();
            employeeRoleExampleCriteria.andEidEqualTo(employee.getEid());
            employeeRoleMapper.deleteByExample(employeeRoleExample);
            // 重新添加employee-role记录
            String rids = employee.getRids();
            String[] ridArray = rids.split(",");
            for (String rid : ridArray) {
                EmployeeRole employeeRole = new EmployeeRole();
                employeeRole.setEid(employee.getEid());
                employeeRole.setRid(Integer.valueOf(rid));
                employeeRoleMapper.insert(employeeRole);
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("修改员工方法结束，出参为{}",result);
        return result;
    }

    /**
     * 删除员工
     * @param eid 员工id
     */
    @Override
    @Transactional
    public BaseResult deleteEmployee(int eid) {
        BaseResult result = new BaseResult();
        log.info("删除员工方法开始，入参为{}", eid);
        try {
            // 不能删除当前登录用户
            Session session = SecurityUtils.getSubject().getSession();
            Employee employee = (Employee) session.getAttribute("employee");
            if (employee.getEid() == eid) {
                result.setMessage("不能删除当前登录用户");
                result.setSuccess(false);
                return result;
            }
            // 删除员工信息
            employeeMapper.deleteByPrimaryKey(eid);
            // 删除员工-角色关联
            EmployeeRoleExample employeeRoleExample = new EmployeeRoleExample();
            EmployeeRoleExample.Criteria employeeRoleExampleCriteria = employeeRoleExample.createCriteria();
            employeeRoleExampleCriteria.andEidEqualTo(eid);
            employeeRoleMapper.deleteByExample(employeeRoleExample);
            result.setSuccess(true);
            result.setMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("操作失败");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("删除员工方法开始，入参为{}", eid);
        return result;
    }

    /**
     * 批量删除员工
     * @param eids 员工id字符串
     */
    @Override
    public BaseResult batchDeleteEmployee(String eids) {
        BaseResult result = new BaseResult();
        log.info("批量删除员工方法开始，入参为{}", eids);
        try {
            eids = eids.substring(0, eids.lastIndexOf(","));
            String[] eidArray = eids.split(",");
            for (String eid : eidArray) {
                BaseResult tempResult = deleteEmployee(Integer.parseInt(eid));
                if (!tempResult.isSuccess()) {
                    throw new Exception();
                }
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            result.setMessage("操作失败");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("批量员工方法开始，出参为{}", result);
        return result;
    }


    /**
     * 导出员工信息
     */
    @Override
    public byte[] exportContent(String eids){
        log.info("导出员工信息方法开始，入参为{}", eids);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 表头
        Row title = sheet.createRow(0);
        title.createCell(0).setCellValue("员工id");
        title.createCell(1).setCellValue("员工姓名");
        title.createCell(2).setCellValue("员工性别");
        title.createCell(3).setCellValue("员工年龄");
        title.createCell(4).setCellValue("员工电话");
        title.createCell(5).setCellValue("入职日期");
        title.createCell(6).setCellValue("员工工号");
        // 获取员工列表 如果eids为空，则导出全部信息
        EmployeeExample example = new EmployeeExample();
        if (eids != null) {
            String[] eidArray = eids.split(",");
            List<Integer> eidList = new ArrayList<>();
            for (String temp : eidArray) {
                eidList.add(Integer.valueOf(temp));
            }
            EmployeeExample.Criteria criteria = example.createCriteria();
            criteria.andEidIn(eidList);
        }
        List<Employee> employeeList = employeeMapper.selectByExample(example);
        for (Employee employee : employeeList) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(employee.getEid());
            row.createCell(1).setCellValue(employee.getEname());
            row.createCell(2).setCellValue(employee.getEsex() == 1 ? "男" : "女");
            row.createCell(3).setCellValue(employee.getEage());
            row.createCell(4).setCellValue(employee.getPhone());
            row.createCell(5).setCellValue(DateUtil.DateToString(employee.getHireDate(), "yyyy年MM月dd日"));
            row.createCell(6).setCellValue(employee.getJobnumber());
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            workbook.write(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("导出员工信息方法结束");
        return byteArrayOutputStream.toByteArray();
    }
}
