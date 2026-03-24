package com.dsu.hope_bank_app_middleware;

import com.dsu.hope_bank_app_middleware.security.entity.Role;
import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.RoleRepository;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.Set;

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

//	@Bean
//	public CommandLineRunner seedUsersAndRoles(
//			RoleRepository roleRepository,
//			UserRepository userRepository,
//			PasswordEncoder passwordEncoder
//	) {
//		return args -> {
//			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
//					.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));
//			Role userRole = roleRepository.findByName("ROLE_USER")
//					.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
//
//			if (!userRepository.existsByUsername("pndungutse")) {
//				userRepository.save(User.builder()
//						.name("pndungutse")
//						.username("0788121212")
//						.env("TURAME")
//						.email("pndungutse@example.com")
//						.password(passwordEncoder.encode("Rwanda@Kigali123"))
//						.roles(Collections.singleton(userRole))
//						.build());
//			}
//
//			if (!userRepository.existsByUsername("admin")) {
//				userRepository.save(User.builder()
//						.name("admin")
//						.username("0788000000")
//						.env("Turame")
//						.email("admin@example.com")
//						.password(passwordEncoder.encode("admin"))
//						.roles(Collections.singleton(adminRole))
//						.build());
//			}
//		};
//	}

}
