package com.version2software.apipolly.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
    RouterFunction<ServerResponse> getRoute() {
        return route()
                .GET("/", controller::getHandler)
                .onError(Exception.class,  controller::statusExceptionHandler)
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> getSoundRoute() {
        return route()
                .GET("/sound/{source}", controller::getSoundHandler)
                .onError(Exception.class,  controller::statusExceptionHandler)
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> postRoute() {
        return route()
                .POST("/", controller::postHandler)
                .onError(Exception.class,  controller::statusExceptionHandler)
                .build();
    }
}
