package com.version2software.apis3.client;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiS3ClientApplication {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.service}")
    private String service;

    public static void main(String[] args) {
        SpringApplication.run(ApiS3ClientApplication.class, args);
    }

    @Bean
    public CloseableHttpClient signingClient() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(service);
        signer.setRegionName(region);

        HttpRequestInterceptor interceptor =
                new AWSRequestSigningApacheInterceptor(service, signer, new DefaultAWSCredentialsProviderChain());

        return HttpClients.custom()
                .addInterceptorLast(interceptor)
                .build();
    }
}
