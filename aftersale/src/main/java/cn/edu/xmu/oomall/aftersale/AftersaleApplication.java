package cn.edu.xmu.oomall.aftersale;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 售后主应用
 *
 * @author wwc
 * @date 2020/11/21 23:22
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.aftersale.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.aftersale.service.impl") //开启dubbo的注解支持
@EnableDiscoveryClient
public class AftersaleApplication {
	public static void main(String[] args) {
		SpringApplication.run(AftersaleApplication.class, args);
	}

}
