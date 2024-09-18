package com.example.backend;

import com.example.backend.Filter.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://localhost:4200") // Adjust if deploying to production
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
	@Bean
	public FilterRegistrationBean<JwtFilter> getFilter() {
		FilterRegistrationBean<JwtFilter> filterReg = new FilterRegistrationBean<>();
		filterReg.setFilter(new JwtFilter());
		filterReg.addUrlPatterns("/api/trips/*", "/api/bills/*", "/api/expenses/*", "/api/members/*");
		filterReg.setOrder(1); // Ensure the filter is invoked early
		return filterReg;
	}

}
