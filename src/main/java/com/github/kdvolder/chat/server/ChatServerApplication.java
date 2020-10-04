package com.github.kdvolder.chat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ChatServerProperties.class)
@SpringBootApplication
public class ChatServerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

}
