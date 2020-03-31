package com.atguigu.mybatis.test;

import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.dao.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

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
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee1 = employeeMapper.getEmpById(1);
            System.out.println(employee1);

            //*********我又想查询一下id为1的员工
            Employee employee2 = employeeMapper.getEmpById(1);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);//true，表示mybatis的一级缓存是存在滴
        } finally {
            sqlSession.close();
        }
    }

    /**
     * mybatis一级缓存：本地缓存，SqlSession级别的缓存，言外之意就是一个SqlSession对象拥有自己的一级缓存，新的SqlSession对象有新的一级缓存
     * 两个一级缓存里面的数据是不能共用的，因此当前一级缓存里面的数据能不能被用到还是一个问题
     * 一级缓存一直是开启着的，我们没办法关闭它
     * 客户端与数据库同一次会话期间查询到的数据会放到本地缓存里面
     * 以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库
     *
     * 一级缓存失效的情况（没有使用到当前一级缓存的情况，效果就是，还需要再向数据库发出查询sql）
     * 1、两次获取相同的数据，但是两次使用的SqlSession对象（底层还是JDBC的java.sql.Connection对象）不同，说白了两次获取相同的数据但是它们使用了不同的数据库连接对象
     * 2、两次使用的SqlSession对象相同，但是两次查询条件不同（原理是因为当前一级缓存里面还没有这个数据）
     * 3、两次使用的SqlSession对象相同，但是两次查询之间执行了增删改操作（原理是因为执行的增删改操作可能对当前数据有影响，相当于一级缓存中的数据有可能是过时的)
     * 4、两次使用的SqlSession对象相同，但是手动清除一级缓存（清空缓存）
     *
     * mybatis二级缓存：全局缓存
     *
     */
    @Test
    public void test2() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        //开启一个客户端与数据库的会话
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        //相当于模拟再开启另一个客户端与数据库的会话
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        Connection connection1 = sqlSession1.getConnection();//SqlSession对象，底层还是JDBC的java.sql.Connection对象
        Connection connection2 = sqlSession2.getConnection();//SqlSession对象，底层还是JDBC的java.sql.Connection对象
        try {
            EmployeeMapper employeeMapper1 = sqlSession1.getMapper(EmployeeMapper.class);
            Employee employee1 = employeeMapper1.getEmpById(1);
            System.out.println(employee1);
            EmployeeMapper employeeMapper2 = sqlSession2.getMapper(EmployeeMapper.class);
            Employee employee2 = employeeMapper2.getEmpById(1);
            System.out.println(employee1);
            System.out.println(employee1 == employee2);//false
            System.out.println(connection1 == connection2);//false
        } finally {
            sqlSession1.close();
            sqlSession2.close();
        }
    }

    @Test
    public void test3() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee1 = employeeMapper.getEmpById(1);
            System.out.println(employee1);
            Employee employee2 = employeeMapper.getEmpById(8);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test4() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee1 = employeeMapper.getEmpById(1);
            System.out.println(employee1);

            Employee employee = new Employee(null,"Rose",'0',"rose@atguigu.com");
            employeeMapper.addEmp(employee);
            System.out.println("添加员工成功。。。");

            Employee employee2 = employeeMapper.getEmpById(1);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);//false
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test5() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee1 = employeeMapper.getEmpById(1);
            System.out.println(employee1);

            //手动清除一级缓存
            sqlSession.clearCache();

            Employee employee2 = employeeMapper.getEmpById(1);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);//false
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

}
