package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Employee;
import org.apache.ibatis.annotations.Select;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月29日
 */
public interface EmployeeMapperAnnotation {

    @Select("select * from tbl_employee where id = #{id}")
    Employee getEmpById(Integer id);

}
