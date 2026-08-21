package com.dsu.hope_bank_app_middleware;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// application.properties (classpath) imports dsumobapp.properties for JWT/T24/etc.
		return application.sources(HopeBankAppMiddlewareApplication.class);
	}

}
