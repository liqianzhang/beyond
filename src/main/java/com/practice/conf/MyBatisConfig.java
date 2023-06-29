package com.practice.conf;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@Configuration
@MapperScan("com.practice.mapper")
public class MyBatisConfig {
    @Resource
    DataSourceProperties properties;


    // com/practice/mapper/UserMapper.xml
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath:com/practice/mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("com.practice.entity");
        return sessionFactory.getObject();
    }


        @Bean
        public DataSource dataSource () {
            //可以在此处调用相关接口获取数据库的配置信息进行 DataSource 的配置
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(properties.getUrl());
            dataSource.setUsername(properties.getUsername());
            dataSource.setPassword(properties.getPassword());
            dataSource.setDriverClassName(properties.getDriverClassName());
            return dataSource;

        }
    }