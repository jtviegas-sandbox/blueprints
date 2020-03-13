package com.tgedr.labs.microservices.blueprint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).securityContexts(securityContext())
				.useDefaultResponseMessages(false).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

	private List<SecurityContext> securityContext() {
		List<SecurityContext> contexts = new ArrayList<>();
		contexts.add(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build());
		return contexts;
	}

	private List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("apikey", authorizationScopes));
	}

}
