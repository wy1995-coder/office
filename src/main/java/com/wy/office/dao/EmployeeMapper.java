package com.wy.office.dao;

import com.wy.office.bean.Employee;
import com.wy.office.bean.EmployeeExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface EmployeeMapper {
    long countByExample(EmployeeExample example);

    int deleteByExample(EmployeeExample example);

    int deleteByPrimaryKey(Integer eid);

    int insert(Employee record);

    int insertSelective(Employee record);

    List<Employee> selectByExample(EmployeeExample example);

    Employee selectByPrimaryKey(Integer eid);

    int updateByExampleSelective(@Param("record") Employee record, @Param("example") EmployeeExample example);

    int updateByExample(@Param("record") Employee record, @Param("example") EmployeeExample example);

    int updateByPrimaryKeySelective(Employee record);

    int updateByPrimaryKey(Employee record);

    /**
     * 工号是否存在
     * @param jobnumber 工号
     * @return
     */
    Employee checkJobNumberUnique(String jobnumber);

    /**
     * 登录
     * @param jobnumber
     * @param password
     * @return
     */
    Employee login(@Param("jobnumber") String jobnumber, @Param("password") String password);

    /**
     * 获取员工列表
     * @param employee
     * @return
     */
    List<Employee> getEmployeeList(Employee employee);

    /**
     * 获取员工信息和对应的角色列表
     * @param eid
     * @return
     */
    Employee getEmployeeAndRoles(int eid);
}