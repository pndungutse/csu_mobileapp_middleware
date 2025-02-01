package com.dsu.hope_bank_app_middleware;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

@SpringBootApplication
public class HopeBankAppMiddlewareApplication {

	public static void main(String[] args) {
		System.out.println("Now Running");

		SpringApplication application = new SpringApplication(HopeBankAppMiddlewareApplication.class);
		application.setDefaultProperties(Collections.singletonMap("spring.config.name", "dsumobapp"));
		application.run(args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public WebMvcConfigurer configure() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry reg) {
				reg.addMapping("/**").allowedOrigins("*");
			}
		};

	}

}
