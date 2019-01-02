package com.catpp.oauth2.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * com.catpp.springbootjpa.base
 *
 * @Author cat_pp
 * @Date 2018/12/25
 * @Description 基础JPA接口
 *
 * - @NoRepositoryBean这个注解如果配置在继承了JpaRepository接口以及其他SpringDataJpa内部的接口的子接口时，子接口
 * 不被作为一个Repository创建代理实现类。
 */
@NoRepositoryBean
public interface BaseRepository<T, PK extends Serializable> extends JpaRepository<T, PK>, JpaSpecificationExecutor<T> {
}
