package com.hairbook;

import com.hairbook.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class HairbookApplication {

	public static void main(String[] args) {
		SpringApplication.run(HairbookApplication.class, args);
	}

}
