package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.entity.Employee;
import com.ghb.reggie.mapper.EmployeeMapper;
import com.ghb.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service("EmployeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

}
