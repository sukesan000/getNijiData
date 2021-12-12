package com.nijiData.NijiData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NijiDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(NijiDataApplication.class, args);
	}

}
