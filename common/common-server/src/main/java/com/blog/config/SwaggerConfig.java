package com.blog.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger", name = "enable", havingValue = "true")
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public Docket restApi() {
        boolean enableSwagger = true;
        if ("prod".equals(profile)) {
            enableSwagger = false;
        }

        List<Parameter> pars = new ArrayList<Parameter>();

        ParameterBuilder langPar = new ParameterBuilder();
        langPar.name("lang").description("语种，中文:zh-CN，英语：en-US")
                .modelRef(new ModelRef("string"))
                .parameterType("header").defaultValue("en-US")
                .required(true).build();
        pars.add(langPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .enable(enableSwagger)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("REST API of Example")
                .description("REST API of Example")
                .termsOfServiceUrl("http://www.merchant.com/terms")
                .license("License of merchant")
                .licenseUrl("http://www.merchant.com/license")
                .version("2.1.0")
                .build();
    }
}
