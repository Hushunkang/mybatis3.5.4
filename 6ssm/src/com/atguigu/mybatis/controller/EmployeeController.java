package com.atguigu.mybatis.controller;

import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	@RequestMapping("/getemps")
	public String emps(){
		List<Employee> emps = employeeService.getEmps();
		Map<String,Object> result = new HashMap<>();
		result.put("allEmps", emps);
		return "list";
	}

}
