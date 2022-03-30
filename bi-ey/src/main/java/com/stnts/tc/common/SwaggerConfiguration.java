//package com.stnts.tc.common;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import io.swagger.annotations.Api;
//import lombok.Setter;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
///**
// * @author liang.zhang
// * @date 2019年9月6日
// * @desc TODO
// */
//@Configuration
//@ConfigurationProperties(prefix="common.swagger")
//public class SwaggerConfiguration {
//
//	@Setter
//	private String title;
//	@Setter
//	private String desc;
//	@Setter
//	private String url;
//	@Setter
//	private String version;
//	@Setter
//	private String username;
//	@Setter
//	private String host;
//	@Setter
//	private String email;
//	
//	@Bean
//    public Docket createRestApi() {
//		
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//    	
//    	Contact contact = new Contact(username, host, email);
//        return new ApiInfoBuilder()
//                .title(title)
//                .description(desc)
//                .termsOfServiceUrl(url)
//                .version(version)
//                .contact(contact)
//                .build();
//    }
//}
