package com.ghb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghb.reggie.common.R;
import com.ghb.reggie.entity.Category;
import com.ghb.reggie.entity.Employee;
import com.ghb.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null){
            return R.error("登录失败，用户不存在！");
        }

        if (!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误！");
        }
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除request中保存的session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee);
        //password为空  给新增员工设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置createTime updateTime createUser updateUser
//        long userId = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(userId);
//        employee.setUpdateUser(userId);

        employeeService.save(employee);
        log.info("新增员工，员工信息：{}",employee);
        return R.success("新增成功");

        //SQLIntegrityConstraintViolationException 当新增用户重复时显示异常
    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Employee> pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工信息 修改按钮 禁用按钮
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long empId =(Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        if (id != null) {
            Employee emp = employeeService.getById(id);
            return R.success(emp);
        }
        return R.error("查询失败");
    }
}
