package cn.edu.xmu.oomall.footprint;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 足迹主应用
 *
 * @author yang8miao
 * @date 2020/11/26 21:24
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.footprint.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.footprint.service.impl") //开启Dubbo的注解支持
@EnableDiscoveryClient
public class FootprintApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootprintApplication.class, args);
	}

}
