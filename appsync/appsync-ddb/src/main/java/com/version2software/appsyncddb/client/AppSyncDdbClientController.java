package com.version2software.appsyncddb.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.unbescape.json.JsonEscape;

import javax.annotation.PostConstruct;
import java.util.Map;

@Controller
public class AppSyncDdbClientController {

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ServerResponse home(ServerRequest serverRequest) {
        return ServerResponse.status(200)
                .render("query", Map.of());
    }

    public ServerResponse post(ServerRequest serverRequest) {
        try {
            String query = serverRequest.param("query").get();
            String jsonRequest = gson.toJson(Map.of("query", query));

            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.addHeader("x-api-key", apiKey);
            httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response =  httpclient.execute(httpPost);

            String content = new String(response.getEntity().getContent().readAllBytes());

            return ServerResponse.status(response.getStatusLine().getStatusCode())
                    .render("query", Map.of(
                                        "query", query,
                                        "request", jsonRequest,
                                        "response", JsonEscape.unescapeJson(prettyPrint(content))));
        } catch (Throwable ex) {
            ex.printStackTrace();
            return ServerResponse.status(500)
                    .render("query", Map.of(
                            "response", ex.getMessage()));
        }
    }

    private String prettyPrint(String s) {
        return gson.toJson(gson.fromJson(s, Map.class));
    }

    @PostConstruct
    private void postConstruct() {
        System.out.println("apiUrl = " + apiUrl);
        System.out.println("apiKey = " + apiKey);
    }
}
