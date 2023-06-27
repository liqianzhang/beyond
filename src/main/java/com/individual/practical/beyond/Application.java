package com.individual.practical.beyond;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class})
@RestController //既然已经引入了web的支持，把它作为restController
@Slf4j
@ComponentScan("com.practice")
public class Application {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@RequestMapping("/sendEmail")
	public String sendEmail() {
		return "success";
	}

	@Bean
	@ConfigurationProperties("niki.datasource")
	public DataSourceProperties nikiDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource nikiDataSource() {
		DataSourceProperties dataSourceProperties = nikiDataSourceProperties();
		log.info("niki dataSource:{}" , dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	@Resource
	public PlatformTransactionManager nikiTxManager(DataSource nikiDataSource) {
		return new DataSourceTransactionManager(nikiDataSource);
	}

	@Bean
	@ConfigurationProperties("bonnie.datasource")
	public DataSourceProperties bonnieDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource bonnieDataSource() {
		DataSourceProperties dataSourceProperties = bonnieDataSourceProperties();
		log.info("bonnie dataSource:{}" , dataSourceProperties.getUrl());

		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	@Resource
	public PlatformTransactionManager bonnieTxManager(DataSource bonnieDataSource) {
		return new DataSourceTransactionManager(bonnieDataSource);
	}
}
