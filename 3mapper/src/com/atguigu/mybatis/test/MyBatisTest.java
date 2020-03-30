package com.atguigu.mybatis.test;

import com.atguigu.mybatis.bean.Employee;
import com.atguigu.mybatis.dao.EmployeeMapper;
import com.atguigu.mybatis.dao.EmployeeMapperAnnotation;
import com.atguigu.mybatis.dao.EmployeeMapperPlus;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 1、根据mybatis的xml配置文件（也称mybatis的全局配置文件）创建一个SqlSessionFactory类的对象实例
     * 2、mybatis的sql映射文件，配置业务sql脚本
     * 3、将mybatis的sql映射文件注册到mybatis的全局配置文件中
     * 4、写代码
     * 4.1、根据mybatis的全局配置文件得到一个SqlSessionFactory类的对象实例
     * 4.2、根据SqlSessionFactory类的对象实例获取SqlSession类的对象实例并使用它来执行增删改查操作
     * 4.3、使用sql唯一标识（namespace+id的策略）来告诉mybatis执行哪一个sql，sql都是保存在mybatis的sql映射文件中
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        //文件就是在类路径（classpath）下，即工程/模块编译的结果的输出的根目录
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //获取SqlSession实例，它能直接执行已经映射的sql脚本
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Employee employee = sqlSession.selectOne("com.atguigu.mybatis.bean.Employee.getEmpById", 1);
            System.out.println(employee);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test2() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            //动态代理的方式生成dao层Mapper接口实现类的对象（即代理实例）
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

            System.out.println(employeeMapper);//org.apache.ibatis.binding.MapperProxy@4abdb505
            System.out.println(employeeMapper.getClass());//class com.sun.proxy.$Proxy4
            //说明：只要把dao层Mapper接口和mybatis的sql映射文件进行绑定，mybatis会为接口动态创建一个代理对象（即这个接口实现类的对象实例）

            Employee emp = employeeMapper.getEmpById(1);
//            Employee emp = employeeMapper.getEmpById(7369);
            System.out.println(emp);
        } finally {
            sqlSession.close();
        }
    }

    /*
     * 1、接口式编程
     * 	原先：		I***Dao		====>  ***DaoImpl
     * 	mybatis：	***Mapper	====>  ***Mapper.xml
     *
     * 2、SqlSession对象代表和数据库一个连接会话，用完必须关闭
     * 3、SqlSession和Connection一样，都是线程不安全的
     *    因为SqlSession底层还是Connection
     *    因此，就不要给它定义单例类的成员变量的形式，因为多线程环境下会出现资源竞争（竞争同一个成员变量）的现象从而出现数据安全问题
     *    因此，每次使用都应该去获取新的SqlSession对象实例
     * 小知识：用一个数据库连接对象来建立jdbc连接其实就是存在着一个数据库客户端与一个数据库服务端的连接会话
     * 4、dao层Mapper接口没有实现类，但mybatis会为接口动态创建一个代理对象（即这个接口实现类的对象实例）
     * 	  前提：把dao层Mapper接口和mybatis的sql映射文件进行绑定
     * 	  EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
     * 5、两个重要的配置文件：
     * 		mybatis的全局配置文件：包含数据库连接池信息，事务管理器信息，系统运行环境信息，可选的文件
     * 		mybatis的sql映射文件：保存了每一个sql语句的映射信息，将业务的关键，sql语句抽取出来，不像Hibernate那样黑箱操作不知道里面sql是啥
     */

    @Test
    public void test3() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperAnnotation employeeMapperAnnotation = sqlSession.getMapper(EmployeeMapperAnnotation.class);
            Employee emp = employeeMapperAnnotation.getEmpById(1);
            System.out.println(emp);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 测试增删改
     * 1、mybatis允许增删改的方法直接定义以下类型的返回值
     * 		Integer、Long、Boolean、void
     * 2、自动提交数据和手动提交数据
     * 	  sqlSessionFactory.openSession(true);自动提交
     * 	  sqlSessionFactory.openSession();手动提交
     */
    @Test
    public void test4() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        //获取到的SqlSession对象是不自动提交的
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //获取到的SqlSession对象是自动提交的
//        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

            //测试添加
//            Employee employee = new Employee(null,"Jerry",'0',"jerry@atguigu.com");
//            employeeMapper.addEmp(employee);

            //测试删除
            employeeMapper.deleteEmp(2);

            //测试更新
            Employee employee = new Employee(1,"Tom",'1',"tom@atguigu.com");
            Boolean isSuccess = employeeMapper.updateEmp(employee);
            System.out.println(isSuccess);

            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test5() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = new Employee(3,"Jerry",'0',"jerry@atguigu.com");
            Integer result = employeeMapper.addEmp(employee);
            System.out.println(result);//0表示添加失败，1表示添加成功
            System.out.println(employee.getId());//返回自增主键的id

            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test6() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = new Employee(null,"Jerry",null,null);
            Integer result = employeeMapper.addEmp(employee);
            System.out.println(result);
            System.out.println(employee.getId());

            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test7() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmpByLastNameAndGender("Tom", '0',"tom@atguigu.com");
            System.out.println(employee);
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test8() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("lastName","Tom");
            paramMap.put("gender",'0');
            paramMap.put("tableName","tbl_employee");
            Employee employee = employeeMapper.getEmpByMap(paramMap);
            System.out.println(employee);
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test9() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            List<Employee> emps = employeeMapper.getEmpsByLastNameLike("%T%");
            for (Employee employee : emps) {
                System.out.println(employee);
            }
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test10() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Map<String,Object> employee = employeeMapper.getEmpByIdReturnMap(1);
            System.out.println(employee);
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test11() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Map<Integer,Employee> emps = employeeMapper.getEmpsByLastNameLikeReturnMap("%T%");
            for(Map.Entry<Integer,Employee> entry : emps.entrySet()){
                System.out.println("key:" + entry.getKey() + ",value:" + entry.getValue());
            }
        }finally {
            sqlSession.close();
        }
    }

    @Test
    public void test12() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapperPlus employeeMapperPlus = sqlSession.getMapper(EmployeeMapperPlus.class);
//            Employee employee = employeeMapperPlus.getEmpById(1);
            Employee employee = employeeMapperPlus.getEmpAndDept(1);
            System.out.println(employee);
            System.out.println(employee.getDept());
        }finally {
            sqlSession.close();
        }
    }

}
