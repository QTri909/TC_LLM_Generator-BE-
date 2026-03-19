package com.group05.TC_LLM_Generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TcLlmGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcLlmGeneratorApplication.class, args);
	}

}
