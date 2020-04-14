package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Employee;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月30日
 */
public interface EmployeeMapperPlus {

    Employee getEmpById(Integer id);

    Employee getEmpAndDept(Integer id);

    Employee getEmpByIdStep(Integer id);

}
