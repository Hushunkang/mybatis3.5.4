package com.atguigu.mybatis.controller;

import com.atguigu.mybatis.bean.Department;
import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	@RequestMapping("/getemps")
	public String emps(Map<String,Object> map){
		List<Employee> emps = employeeService.getEmps();
		map.put("allEmps", emps);
		return "list";
	}

	@RequestMapping("/addEmp")
	public String addEmp(){
		Employee employee = new Employee(null,"abc",'0',"abc@atguigu.com",new Department(1));
		employeeService.addEmp(employee);
		return "list";
	}

}
