package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Employee;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月29日 07时13分25秒
 */
public interface EmployeeMapper {

    Employee getEmpById(Integer id);

    Integer addEmp(Employee emp);

    Long deleteEmp(Integer id);

    Boolean updateEmp(Employee emp);

//    void updateEmp(Employee emp);

}
