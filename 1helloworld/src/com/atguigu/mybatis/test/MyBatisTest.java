package com.atguigu.mybatis.test;

import com.atguigu.mybatis.bean.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月28日 18时26分32秒
 */
public class MyBatisTest {

    /**
     * 1、根据mybatis的xml配置文件（也称mybatis的全局配置文件）创建一个SqlSessionFactory类的对象实例
     * 2、mybatis的sql映射文件，配置业务sql脚本
     * 3、将mybatis的sql映射文件注册到mybatis的全局配置文件中
     * 4、写代码
     * 4.1、根据mybatis的全局配置文件得到一个SqlSessionFactory类的对象实例
     * 4.2、根据SqlSessionFactory类的对象实例获取SqlSession实例并使用它来执行增删改查操作
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
            Employee employee = sqlSession.selectOne("com.atguigu.mybatis.bean.Employee.selectEmp", 1);
            System.out.println(employee);
        } finally {
            sqlSession.close();
        }
    }

}
