package com.emmett.customermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;

@Configuration
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(PathSelectors.any())
          .build()
                .apiInfo(new ApiInfo("Customer API", "Customer API developer reference docs", "1.0", "",
                        new Contact("Emmett Walsh","", null), "", "", Arrays.asList(new VendorExtension() {
                    @Override
                    public String getName() {
                        return "";
                    }

                    @Override
                    public Object getValue() {
                        return "null";
                    }
                })));
    }
}