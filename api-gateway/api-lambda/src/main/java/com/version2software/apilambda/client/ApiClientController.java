package com.version2software.apilambda.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ApiClientController {

    @Value("${api.url}")
    private String apiUrl;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public ApiClientController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ServerResponse proxyHandler(ServerRequest req) throws JsonProcessingException {
        System.out.println("Enter proxyHandler");

        String status = req.pathVariable("status");
        String url = apiUrl + "proxy/" + status;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        List<Map.Entry<String, List<String>>> headersList = responseHeaders.entrySet().stream().collect(Collectors.toList());
        Map httpBody = responseEntity.getBody();

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(httpBody);

        System.out.println("Exit proxyHandler");
        return ServerResponse.status(httpStatus)
                .render("index", Map.of("response", json,
                        "headers", headersList,
                        "status", httpStatus));
    }

    public ServerResponse nonproxyHandler(ServerRequest req) throws JsonProcessingException {
        System.out.println("Enter nonproxyHandler");

        String status = req.pathVariable("status");
        String url = apiUrl + "nonproxy/" + status;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<Object> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        List<Map.Entry<String, List<String>>> headersList = responseHeaders.entrySet().stream().collect(Collectors.toList());

        headersList.stream().forEach(System.out::println);

        Object httpBody = responseEntity.getBody();

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(httpBody);
        System.out.println("Exit nonproxyHandler");

        return ServerResponse.status(httpStatus)
                .render("index",
                        Map.of("response", json,
                                "headers", headersList,
                                "status", httpStatus));
    }

    ServerResponse statusExceptionHandler(Throwable throwable, ServerRequest req) {
        System.out.println("Enter statusExceptionHandler: " + throwable);

        HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) throwable;
        HttpStatus responseStatus = httpStatusCodeException.getStatusCode();
        String responseBodyAsString = httpStatusCodeException.getResponseBodyAsString();

        HttpHeaders responseHeaders = httpStatusCodeException.getResponseHeaders();
        List<Map.Entry<String, List<String>>> headersList = responseHeaders.entrySet().stream().collect(Collectors.toList());

        Map map = new Gson().fromJson(responseBodyAsString, Map.class);

        String json = null;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("Exit statusExceptionHandler");

        return ServerResponse.status(responseStatus)
                .render("index",
                        Map.of(
                                "response", json,
                                "headers", headersList,
                                "status", responseStatus));
    }

}
