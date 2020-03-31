package com.atguigu.mybatis.test;

import com.atguigu.mybatis.bean.Department;
import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.dao.EmployeeMapperDynamicSQL;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月28日 18时26分32秒
 */
public class MyBatisTest {

    private SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        return new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void test1() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperDynamicSQL employeeMapperDynamicSQLMapper = sqlSession.getMapper(EmployeeMapperDynamicSQL.class);
            Employee employee = new Employee(null,null,'0',"");
//            List<Employee> emps = employeeMapperDynamicSQLMapper.getEmpsByConditionIf(employee);
//            List<Employee> emps = employeeMapperDynamicSQLMapper.getEmpsByConditionTrim(employee);
            List<Employee> emps = employeeMapperDynamicSQLMapper.getEmpsByConditionChoose(employee);

            //查询的时候如果某些条件没带可能sql拼装会有问题
            //解决方案1、给where后面加上1=1，后面每个if分支都是and ***
            //解决方案2、mybatis使用where标签来将所有的查询条件包括在内，mybatis就会将where标签中拼装的sql脚本，多出来的and或者or自动去掉
            //细节：where标签只会去掉第一个多出来的and或者or

            for (Employee emp : emps) {
                System.out.println(emp);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test2() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperDynamicSQL employeeMapperDynamicSQLMapper = sqlSession.getMapper(EmployeeMapperDynamicSQL.class);
            Employee employee = new Employee(1,"TomUpdate",'0',"tom@atguigu.com");
            employeeMapperDynamicSQLMapper.updateEmp(employee);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test3() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperDynamicSQL employeeMapperDynamicSQLMapper = sqlSession.getMapper(EmployeeMapperDynamicSQL.class);
            List<Employee> emps = employeeMapperDynamicSQLMapper.getEmpsByConditionForeach(Arrays.asList(1, 8));
            for (Employee emp : emps) {
                System.out.println(emp);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test4() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperDynamicSQL employeeMapperDynamicSQLMapper = sqlSession.getMapper(EmployeeMapperDynamicSQL.class);
            List<Employee> emps = new ArrayList<>();
            emps.add(new Employee(null,"Jack2",'1',"jack2@atguigu.com",new Department(1)));
            emps.add(new Employee(null,"Jack2",'1',"jack2@atguigu.com",new Department(1)));
            employeeMapperDynamicSQLMapper.addEmps(emps);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

}
