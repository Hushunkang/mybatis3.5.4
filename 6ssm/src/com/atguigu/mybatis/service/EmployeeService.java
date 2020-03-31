package com.atguigu.mybatis.service;

import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.dao.EmployeeMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private SqlSession sqlSession;
	
	public List<Employee> getEmps(){
		//EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
		return employeeMapper.getEmps();
	}

	@Transactional(isolation=Isolation.READ_COMMITTED,readOnly=false,timeout=-1)
	public void addEmp(Employee employee){
		EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
		mapper.addEmp(employee);
		System.out.println(1 / 0);
		System.out.println("造个异常看能不能回滚。。。");
	}

}
