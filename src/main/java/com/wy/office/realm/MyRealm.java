package com.wy.office.realm;

import com.wy.office.bean.Employee;
import com.wy.office.bean.EmployeeExample;
import com.wy.office.bean.Permission;
import com.wy.office.bean.Role;
import com.wy.office.dao.EmployeeMapper;
import com.wy.office.dao.RoleMapper;
import com.wy.office.service.PermissionService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 获取用户对应的角色
        Employee employee = (Employee) principalCollection.getPrimaryPrincipal();
        List<Role> roleByeid = roleMapper.getRoleByeid(employee.getEid());
        for (Role role : roleByeid) {
            authorizationInfo.addRole(role.getRcode());
        }
        List<Permission> perms = permissionService.getPermsListByeid(employee.getEid());
        for (Permission permission : perms) {
            authorizationInfo.addStringPermission(permission.getPcode());
        }
        return authorizationInfo;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        SimpleAuthenticationInfo authenticationInfo = null;
        // 获取页面传入的用户名密码封装对象token
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 查询用户信息
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andJobnumberEqualTo(token.getUsername());
        List<Employee> employees = employeeMapper.selectByExample(example);
        if (employees != null && employees.size() > 0) {
            Employee employee = employees.get(0);
            authenticationInfo = new SimpleAuthenticationInfo(employee, employee.getPassword(), token.getUsername());
            // 获取盐值
            String salt = employee.getRemark1();
            authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(salt));

        }
        return authenticationInfo;
    }
}
