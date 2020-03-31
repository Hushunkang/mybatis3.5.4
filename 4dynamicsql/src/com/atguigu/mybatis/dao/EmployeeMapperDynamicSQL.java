package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月31日 06时57分55秒
 */
public interface EmployeeMapperDynamicSQL {

     List<Employee> getEmpsTestInnerParameter(Employee employee);

    //employee携带了哪些字段，查询条件就带上这些字段
     List<Employee> getEmpsByConditionIf(Employee employee);

     List<Employee> getEmpsByConditionTrim(Employee employee);

     List<Employee> getEmpsByConditionChoose(Employee employee);

     void updateEmp(Employee employee);

    //查询员工id'在给定集合中的
     List<Employee> getEmpsByConditionForeach(@Param("ids")List<Integer> ids);

     void addEmps(@Param("emps")List<Employee> emps);

}
