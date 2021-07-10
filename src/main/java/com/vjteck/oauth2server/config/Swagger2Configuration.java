package com.vjteck.oauth2server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Title: swagger2 configuration 
 * Description: Copyright: Copyright (c) das.vjteck 2021
 * Company: vjteck
 *
 * @author jimmytseng
 * @version v1.0
 * @date 2021-06-21
 */
@EnableSwagger2
@Configuration
public class Swagger2Configuration {

	@Bean
	public Docket createRestApi() {

		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(true).select()
				.apis(RequestHandlerSelectors.basePackage("com.vjteck.oauth2server.controller"))
				.paths(Predicates.not(PathSelectors.regex("/error.*"))).paths(PathSelectors.any()).build();

		return docket;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("VJ TECK").description("").termsOfServiceUrl("").version("V1.0").build();
	}

}
