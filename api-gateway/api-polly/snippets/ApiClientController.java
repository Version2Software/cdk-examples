package com.version2software.apipolly.client;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ApiClientController {

    @Value("${api.url}")
    private String apiUrl;
//
//    @Value("${sound.dir}")
//    private String soundDir;

    //    @PostConstruct
//    private void cleanup() throws IOException {
//        // Clean out old temp sound files
//        File[] files = Path.of(soundDir).toFile().listFiles();
//        for (File file : files) {
//            if (file.getName().startsWith("phrase") && file.getName().endsWith("mp3")) {
//                Files.delete(file.toPath());
//            }
//        }
//    }

    private ConcurrentHashMap<String, byte[]> soundMap = new ConcurrentHashMap<>();

    public ServerResponse getHandler(ServerRequest req) {
        System.out.println("enter getHandler");
        return ServerResponse.status(200)
                .render("polly", Map.of());
    }

    public ServerResponse postHandler(ServerRequest req) throws IOException {
        String phrase = req.param("phrase").get();
        String voice = req.param("voice").get();

        try(final CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            Map map = Map.of("Text", phrase, "VoiceId", voice, "OutputFormat", "mp3");
            httpPost.setEntity(new StringEntity(new Gson().toJson(map)));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "audio/mpeg");

            try(final CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity httpEntity = response.getEntity();
                try (InputStream in = httpEntity.getContent()) {
//                    System.out.println("uuid = " + UUID.randomUUID().toString());
                    // Alternate design:
                    // Construct a concurrent map at startup
                    // Create a random key
                    // concurrent map.put(randomkey, bytes) src=/sound/randomkey
                    // in soundHandler bytes=map.get(randomkey)
                    String key = UUID.randomUUID().toString();
                    soundMap.put(key, in.readAllBytes());
//                    File tempFile = File.createTempFile("phrase",
//                            ".mp3", Path.of(soundDir).toFile());
//
//                    FileOutputStream fos = new FileOutputStream(tempFile);
//                    fos.write(in.readAllBytes());
//                    fos.close();

                    return ServerResponse.status(200)
                            .render("polly", Map.of(
                                    "phrase", phrase,
                                    "voice", voice,
                                    "source", "/sound/" + key));
//                                    "source", "/sound/" + tempFile.getName()));
                }
            }
        }
    }

    ServerResponse statusExceptionHandler(Throwable throwable, ServerRequest req) {
        System.out.println("Enter statusExceptionHandler: " + throwable);

        HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) throwable;
        HttpStatus responseStatus = httpStatusCodeException.getStatusCode();
        String responseBodyAsString = httpStatusCodeException.getResponseBodyAsString();

        System.out.println("Exit statusExceptionHandler");

        return ServerResponse.status(responseStatus)
                .render("polly",
                        Map.of("phrase", responseBodyAsString));
    }

    public ServerResponse getSoundHandler(ServerRequest serverRequest) throws IOException {
        System.out.println("Enter getSoundHandler");

        String source = serverRequest.pathVariable("source");
//        byte[] bytes = Files.readAllBytes(Path.of(soundDir + "/" + source ));
        byte[] bytes = soundMap.get(source);

        System.out.println("Exit getSoundHandler");

        return ServerResponse.status(200)
                .contentType(MediaType.valueOf("audio/mpeg"))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
