package com.softbinator_labs.project.good_deeds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GoodDeedsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodDeedsApplication.class, args);
	}

}
