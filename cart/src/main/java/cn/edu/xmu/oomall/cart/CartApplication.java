package cn.edu.xmu.oomall.cart;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 购物车主应用
 *
 * @author yang8miao
 * @date 2020/11/29 00:10
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.cart.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.cart.service.impl") //开启Dubbo的注解支持
@EnableDiscoveryClient
public class CartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}

}
