package com.version2software.appsyncddb.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class AppSyncDdbClientApplication {

    @Autowired
    AppSyncDdbClientController controller;

    public static void main(String[] args) {
        SpringApplication.run(AppSyncDdbClientApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> home() {
        return route()
                .GET("/query", controller::home)
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> post() {
        return route()
                .POST("/query", controller::post)
                .build();
    }
}
