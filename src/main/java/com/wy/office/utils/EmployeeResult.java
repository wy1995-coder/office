package com.wy.office.utils;

import com.wy.office.bean.Employee;

public class EmployeeResult extends BaseResult {

    private boolean loginSuccess;
    private Employee employee;

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "EmployeeResult{" +
                "loginSuccess=" + loginSuccess +
                ", employee=" + employee +
                '}';
    }
}
