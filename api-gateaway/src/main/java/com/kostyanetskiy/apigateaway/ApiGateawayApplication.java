package com.kostyanetskiy.apigateaway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ApiGateawayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGateawayApplication.class, args);
    }

}
