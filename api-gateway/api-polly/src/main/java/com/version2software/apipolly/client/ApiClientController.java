package com.version2software.apipolly.client;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
public class ApiClientController {

    @Value("${api.url}")
    private String apiUrl;

    private ConcurrentHashMap<String, byte[]> soundMap = new ConcurrentHashMap<>();

    private List<String> voices;

    @PostConstruct
    private void getVoices() throws IOException, URISyntaxException {
        try(final CloseableHttpClient client = HttpClients.createDefault()) {

            URIBuilder builder = new URIBuilder(apiUrl);
            // TODO - Add querystring to api method request
//            builder.setParameter("Engine", "standard");

            HttpGet httpGet = new HttpGet(builder.build());

            try(final CloseableHttpResponse response = client.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                try (InputStream in = httpEntity.getContent()) {
                    String json = new String(in.readAllBytes());
                    Map<String, List<Map>> map = new Gson().fromJson(json, Map.class);
                    List<Map> list = map.get("Voices");

                    voices = list.stream()
                            .filter(v -> ((String) v.get("LanguageCode")).startsWith("en"))
                            .map(v -> v.get("Id") + " [" + v.get("LanguageCode") + "]")
                            .sorted()
                            .collect(Collectors.toList());
                }
            }
        }
    }

    public ServerResponse getHandler(ServerRequest req) {
        return ServerResponse.status(200)
                .render("polly", Map.of("voices", voices));
    }

    public ServerResponse postHandler(ServerRequest req) throws IOException {
        String phrase = req.param("phrase").get();
        String voiceCountry = req.param("voice").get();
        String voice = voiceCountry.split(" ")[0];

        try(final CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            Map map = Map.of("Text", phrase, "VoiceId", voice, "OutputFormat", "mp3");
            String json = new Gson().toJson(map);

            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "audio/mpeg");

            try(final CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity httpEntity = response.getEntity();
                try (InputStream in = httpEntity.getContent()) {

                    String key = UUID.randomUUID().toString();

                    // This technique would eventually run out of Java memory in a production system.
                    // An alternative would be to create a temp file using the key as part of the filename.
                    soundMap.put(key, in.readAllBytes());

                    return ServerResponse.status(200)
                            .render("polly", Map.of(
                                    "phrase", phrase,
                                    "voices", voices,
                                    "voice", voiceCountry,
                                    "source", "/sound/" + key));
                }
            }
        }
    }

    public ServerResponse getSoundHandler(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        byte[] bytes = soundMap.get(source);

        return ServerResponse.status(200)
                .contentType(MediaType.valueOf("audio/mpeg"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    ServerResponse statusExceptionHandler(Throwable throwable, ServerRequest req) {
        throwable.printStackTrace();
        return ServerResponse.status(500)
                .render("polly",
                        Map.of());
    }
}
