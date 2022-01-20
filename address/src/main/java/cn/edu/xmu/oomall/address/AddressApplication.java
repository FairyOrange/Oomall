package cn.edu.xmu.oomall.address;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 地址主应用
 *
 * @author wwc
 * @date 2020/11/24 23:27
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.address.mapper")
@EnableDubbo(scanBasePackages  = "cn.edu.xmu.oomall.address.service.impl") //开启Dubbo的注解支持
@EnableDiscoveryClient
public class AddressApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressApplication.class, args);
	}

}
