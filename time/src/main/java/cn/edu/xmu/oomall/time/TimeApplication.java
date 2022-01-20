package cn.edu.xmu.oomall.time;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 时间主应用
 *
 * @author wwc
 * @date 2020/11/24 23:27
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.time.mapper")
//必须配置包扫描、否则Dubbo无法注册服务
@EnableDubbo(scanBasePackages  = "cn.edu.xmu.oomall.time.service.impl") //开启Dubbo的注解支持
@EnableDiscoveryClient
public class TimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeApplication.class, args);
	}

}
