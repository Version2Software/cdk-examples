package com.version2software.apilambda.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class ApiClientApplication {

    @Autowired
    ApiClientController controller;

    public static void main(String[] args) {
        SpringApplication.run(ApiClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    RouterFunction<ServerResponse> proxyRoute() {
        return route()
                .GET("/proxy/{status}", controller::proxyHandler)
                .onError(Exception.class,  controller::statusExceptionHandler)
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> nonproxyRoute() {
        return route()
                .GET("/nonproxy/{status}", controller::nonproxyHandler)
                .onError(Exception.class,  controller::statusExceptionHandler)
                .build();
    }
}
