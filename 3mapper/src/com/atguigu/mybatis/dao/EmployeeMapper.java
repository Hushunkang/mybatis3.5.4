package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    Employee getEmpByLastNameAndGender(@Param("lastName") String lastName, @Param("gender") Character gender, String email);

    Employee getEmpByMap(Map paramMap);

    List<Employee> getEmpsByLastNameLike(String lastName);

    //单条记录封装成一个map
    Map<String,Object> getEmpByIdReturnMap(Integer id);

    //多条记录封装成一个map
    @MapKey("id")//@MapKey注解告诉mybatis，把查询的结果封装成map时，map中的key是pojo对象的哪个属性
    Map<Integer,Employee> getEmpsByLastNameLikeReturnMap(String lastName);

    List<Employee> getEmpsByDeptId(Integer deptId);

}
