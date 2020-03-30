package com.atguigu.mybatis.dao;

import com.atguigu.mybatis.bean.Department;

/**
 * @author hskBeginner Email：2752962035@qq.com
 * @version 1.0
 * @description
 * @create 2020年03月30日 16时21分04秒
 */
public interface DepartmentMapper {

    Department getDeptById(Integer id);

}
