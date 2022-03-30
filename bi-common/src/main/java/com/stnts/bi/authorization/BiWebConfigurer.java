package com.stnts.bi.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liang.zhang
 * @date
 */
@Configuration
public class BiWebConfigurer implements WebMvcConfigurer{

	@Bean
	public AuthInterceptor authInterceptor() {
		return new AuthInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor())
				.excludePathPatterns("/api/listDepartmentByOrgId")
				.excludePathPatterns("/api/listDmByUserId")
				.excludePathPatterns("/api/delDmByCcid")
				.addPathPatterns("/**");
	}
}
