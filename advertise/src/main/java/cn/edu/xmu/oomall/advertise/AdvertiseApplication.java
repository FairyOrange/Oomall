package cn.edu.xmu.oomall.advertise;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo //开启dubbo的注解支持
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall"})
@MapperScan("cn.edu.xmu.oomall.advertise.mapper")
@EnableDiscoveryClient
public class AdvertiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvertiseApplication.class, args);
	}

}
