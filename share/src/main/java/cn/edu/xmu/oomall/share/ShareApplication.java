package cn.edu.xmu.oomall.share;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.share.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.share.service.impl") //开启Dubbo的注解支持
@EnableDiscoveryClient
public class ShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareApplication.class, args);
	}

}
