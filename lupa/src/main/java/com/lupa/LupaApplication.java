package com.lupa;


import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import com.my.netty.NettyServerInit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {"com.lupa"})
@SpringBootApplication
@EnableMPP
@MapperScan("com.lupa.mapper")
//@SpringBootApplication
public class LupaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LupaApplication.class, args);
		NettyServerInit nettyServerInit = new NettyServerInit();
		nettyServerInit.init();

	}

}
