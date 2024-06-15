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
		System.out.println("    _                     _                 _    \n" +
				"   | |    _  _     ___   | |_     __ _     (_)   \n" +
				"   | |   | +| |   / _ \\  | ' \\   / _` |    | |   \n" +
				"  _|_|_   \\_,_|   \\___/  |_||_|  \\__,_|   _|_|_  \n" +
				"_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"| \n" +
				"\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'  ");
		SpringApplication.run(LupaApplication.class, args);
		NettyServerInit nettyServerInit = new NettyServerInit();
		nettyServerInit.init();

	}

}
