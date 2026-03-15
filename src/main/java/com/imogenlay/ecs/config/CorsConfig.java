package com.imogenlay.ecs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer
{
	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:5173", "https://imogenlay.net", "https://dg4zi1eumu0qg.cloudfront.net")
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}