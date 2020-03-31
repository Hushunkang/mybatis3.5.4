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
     * mybatis二级缓存：全局缓存，namespace级别的缓存，一个namespace对应着一个mybatis的sql映射文件，一个namespace对应着一个二级缓存
     * 工作机制：
     *  1、一个sql会话，查询一条数据，这个数据就会被放在当前sql会话的一级缓存中
     * 	2、如果sql会话关闭，当前sql会话的一级缓存中的数据就会被保存到二级缓存中，新的sql会话查询信息，就可以参照二级缓存中的数据
     * 	3、mybatis的二级缓存之namespace级别的缓存
     * 	                EmployeeMapper===>Employee
     * 	                DepartmentMapper===>Department
     * 			        不同namespace查出的数据会放在自己对应的二级缓存中（缓存也是一个对象，mybatis中二级缓存的本质就是一个map，很多缓存的设计思想类似，差不多都是map）
     * 	使用：
     * 	1）、在mybatis的全局配置文件中配置<setting name="cacheEnabled" value="true"/>
     * 	2）、去***Mapper.xml中配置使用二级缓存<cache></cache>，在哪个mybatis的sql映射文件中配置了这个cache标签，哪个namespace就有二级缓存的功能，否则没有二级缓存的功能
     * 	3）、我们的POJO类需要实现序列化接口
     * 	效果：
     * 	数据会从二级缓存中获取，查出的数据都会被默认先放在一级缓存中，只有sql会话提交或者关闭以后，一级缓存中的数据才会转移到二级缓存中
     *
     * 和缓存有关的设置/属性：
     * 			1）、cacheEnabled=true：false：关闭缓存（二级缓存关闭）(一级缓存一直可用的)
     * 			2）、每个select标签都有useCache="true"：
     * 					false：不使用缓存（一级缓存依然使用，二级缓存不使用）
     * 			3）、【每个增删改标签的：flushCache="true"：（一级二级都会清除）】
     * 					增删改执行完成后就会清楚缓存；
     * 					测试：flushCache="true"：一级缓存就清空了；二级也会被清除；
     * 					查询标签：flushCache="false"：
     * 						如果flushCache=true;每次查询之后都会清空缓存；缓存是没有被使用的；
     * 			4）、sqlSession.clearCache();只是清楚当前session的一级缓存；
     * 			5）、localCacheScope：本地缓存作用域：（一级缓存SESSION）；当前会话的所有数据保存在会话缓存中；
     * 								STATEMENT：可以禁用一级缓存；
     *
     *第三方缓存整合：
     *		1）、导入第三方缓存包即可；
     *		2）、导入与第三方缓存整合的适配包；官方有；
     *		3）、mapper.xml中使用自定义缓存
     *		<cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
     *
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

    @Test
    public void test6() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();
        try {
            EmployeeMapper employeeMapper1 = sqlSession1.getMapper(EmployeeMapper.class);
            //第一次查询 Cache Hit Ratio 缓存命中率 0
            Employee employee1 = employeeMapper1.getEmpById(1);
            System.out.println(employee1);
            sqlSession1.close();

            EmployeeMapper employeeMapper2 = sqlSession2.getMapper(EmployeeMapper.class);
            //第二次查询是从二级缓存中获取的数据，并没有重新发送sql Cache Hit Ratio 缓存命中率 1/2
            Employee employee2 = employeeMapper2.getEmpById(1);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);//若二级缓存的readOnly属性设置为true这个地方返回true，否则返回false

            EmployeeMapper employeeMapper3 = sqlSession2.getMapper(EmployeeMapper.class);
            //第三次查询是从二级缓存中获取的数据，并没有重新发送sql Cache Hit Ratio 缓存命中率 2/3
            Employee employee3 = employeeMapper3.getEmpById(1);
            System.out.println(employee3);
            System.out.println(employee1 == employee3);//若二级缓存的readOnly属性设置为true这个地方返回true，否则返回false
        } finally {
            sqlSession1.close();
            sqlSession2.close();
            sqlSession3.close();
        }
    }

}
