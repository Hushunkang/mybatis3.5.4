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
 * @create 2020年03月28日
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
     * 一级缓存默认是开启着的
     * 客户端与数据库同一次会话期间查询到的数据会放到本地缓存里面
     * 以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库
     *
     * 一级缓存失效的情况（没有使用到当前一级缓存的情况，效果就是，还需要再向数据库发出查询sql）
     * 1、两次获取相同的数据，但是两次使用的SqlSession对象（底层还是JDBC的java.sql.Connection对象）不同，说白了两次获取相同的数据（两次JDBC操作用的是不同的数据库连接对象）但是它们使用了不同的数据库连接对象
     * 2、两次使用的SqlSession对象相同，但是两次查询条件不同（原理是因为当前一级缓存里面还没有这个数据）
     * 3、两次使用的SqlSession对象相同，但是两次查询之间执行了增删改操作（原理是因为执行的增删改操作可能对当前数据有影响，相当于一级缓存中的数据有可能是过时的)
     * 4、两次使用的SqlSession对象相同，但是手动清除一级缓存（清空缓存）
     *
     * 如果多个sqlSession对象之间需要共享缓存，则需要使用到二级缓存，二级缓存实现了多个SqlSession对象之间共享缓存
     * 如何理解二级缓存实现了多个SqlSession对象之间共享缓存
     * 若某个namespace开启了二级缓存的功能，不同的SqlSession对象去操作这个namespace下的某个查询操作，并且最终执行的查询sql是一样的
     * 并且通过“只有sql会话提交或者关闭以后，一级缓存中的数据才会被转移到二级缓存中”
     * 这种手段将某一个SqlSession对象中的数据，即一级缓存中的数据转移到了二级缓存
     * 以后这个namespace下的所有的SqlSession对象只要是最终执行的查询sql是一样的
     * 查询会拿到二级缓存中的数据，不会发送sql查询数据库，这就是二级缓存实现了多个SqlSession对象之间共享缓存
     * 说白了就是共享二级缓存！！！
     * mybatis二级缓存：全局缓存，namespace级别的缓存，一个namespace对应着一个mybatis的sql映射文件，一个namespace对应着一个二级缓存
     * 工作机制：
     *  1、一个sql会话，查询一条数据，这个数据就会被放在当前sql会话的一级缓存中
     * 	2、如果sql会话关闭，当前sql会话的一级缓存中的数据就会被保存到二级缓存中，新的sql会话查询信息，就可以参照二级缓存中的数据
     * 	3、mybatis的二级缓存之namespace级别的缓存
     * 	                EmployeeMapper===>Employee
     * 	                DepartmentMapper===>Department
     * 			        不同namespace查出的数据会放在自己对应的二级缓存（缓存本质也是对象）中
     *
     * 	使用：
     * 	1）、在mybatis的全局配置文件中配置<setting name="cacheEnabled" value="true"/>
     * 	2）、去***Mapper.xml中配置使用二级缓存<cache></cache>，在哪个mybatis的sql映射文件中配置了这个cache标签，哪个namespace就有二级缓存的功能，否则没有二级缓存的功能
     * 	3）、我们的POJO类需要实现序列化接口
     *
     * 	开启mybatis二级缓存的最终效果：
     * 	mybatis在查询数据的时候，会先从二级缓存中查询数据
     * 	若从二级缓存中查出数据，则数据都会被默认先放在一级缓存中，完成查询任务
     * 	若从二级缓存中查不出数据，则去一级缓存中查询数据，查出数据，完成查询任务
     * 	若从一级缓存中还查不出数据，则发送sql查询数据库，查询出的数据放到一级缓存里面，完成查询任务
     * 	注意：只有sql会话提交或者关闭以后，一级缓存中的数据才会被转移到二级缓存中
     *
     * 	补充：mybatis一级缓存的底层数据结构是键值对的，它的key是StatementId + Offset + Limit + Sql + Params，它的value是查询出的数据
     * 	     只要两条SQL的这五个值拼接一起是相同的，即可以认为是相同的查询操作
     * 	     泛泛的讲，在一级缓存没有失效的前提下，第一次查询数据发送sql查询数据库，后面相同的查询操作不用发送sql查询数据库
     *       myBatis一级缓存的生命周期和SqlSession一致
     *       myBatis一级缓存内部设计简单，只是一个没有容量限定的HashMap，在缓存的功能性上有所欠缺
     *       myBatis一级缓存最大范围是SqlSession内部，有多个SqlSession或者微服务架构下，数据库写操作会引起读到脏数据的问题，建议设定mybatis一级缓存的级别为Statement
     *       即在mybatis全局配置文件中添加如下设置<setting name="localCacheScope" value="STATEMENT"/>，而不是默认的SESSION级别<setting name="localCacheScope" value="SESSION"/>
     *       其实建议在生产环境上面禁用掉mybatis的缓存机制，单纯的当一个ORM框架使用即可
     * 	     参考资料：https://tech.meituan.com/2018/01/19/mybatis-cache.html
     *
     * 和缓存有关的设置/属性：
     *  1）、mybatis的全局配置文件中cacheEnabled=true/false：是否开启还是关闭二级缓存（一级缓存一直可用的）
     * 	2）、每个select标签都有useCache=true/false，设置成false表示不使用二级缓存（不使用二级缓存，依然使用一级缓存）
     * 	3）、每个增删改标签都有flushCache=true，默认为true表示增删改执行完成后就会清除mybatis一级缓存和mybatis二级缓存中的数据
     * 		每个查询标签都有flushCache=false，默认为false表示每次查询之后mybatis一级缓存和mybatis二级缓存都不会被清除
     * 	    如果在查询标签中设置flushCache=true表示每次查询之后都会清除mybatis一级缓存和mybatis二级缓存中的数据，即mybatis的缓存是没有被使用的
     * 	4）、sqlSession.clearCache()只是清除当前sql会话的一级缓存
     * 	5）、localCacheScope本地缓存作用域，即表示mybatis一级缓存
     * 	                    设置成SESSION表示当前sql会话的所有数据保存在sql会话缓存中
     * 					    设置成STATEMENT表示可以禁用一级缓存
     *
     * mybatis与第三方缓存整合（mybatis自己预留了Cache接口，给自己留条后路，毕竟自己不是专业做缓存的，预留的Cache接口就是为了整合第三方缓存）：
     *	1）、导入第三方缓存包ehcache-core-2.6.8.jar
     *	2）、导入与第三方缓存整合的适配包，适配包在mybatis在github上面地址里面有下载mybatis-ehcache-1.0.3.jar
     *	3）、***Mapper.xml中使用自定义缓存
     *		 <cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
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
            //第一次查询是从数据库中获取的数据，发送sql Cache Hit Ratio 缓存命中率 0
            Employee employee1 = employeeMapper1.getEmpById(1);//发送sql查询数据库，查询出的数据放到一级缓存里面，完成查询任务
            System.out.println(employee1);
//            sqlSession1.close();//注意：只有sql会话提交或者关闭以后，一级缓存中的数据才会被转移到二级缓存中

            EmployeeMapper employeeMapper2 = sqlSession2.getMapper(EmployeeMapper.class);
            //第二次查询是从二级缓存中获取的数据，并没有重新发送sql Cache Hit Ratio 缓存命中率 1/2
            Employee employee2 = employeeMapper2.getEmpById(1);
            System.out.println(employee2);
            System.out.println(employee1 == employee2);//若二级缓存的readOnly属性设置为true这个地方返回true，否则返回false

            EmployeeMapper employeeMapper3 = sqlSession3.getMapper(EmployeeMapper.class);
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
