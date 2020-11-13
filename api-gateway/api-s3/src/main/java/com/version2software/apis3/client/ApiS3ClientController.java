package com.version2software.apis3.client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ApiS3ClientController {

    @Value("${api.url}")
    private String apiUrl;

    @Value("${file.download.dir}")
    private String fileDownloadDir;

    private CloseableHttpClient signingClient;

    public ApiS3ClientController(CloseableHttpClient signingClient) {
        this.signingClient = signingClient;
    }

    @GetMapping("/")
    public String home() {
        return "s3";
    }

    @PostMapping("/ListAllBuckets")
    public String listAllBuckets(Model model) {
        try {
            HttpGet httpGet = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = signingClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                String content = getContent(response.getEntity());
                populateModel(model, "", "", content, headers, statusCode);
            }

        } catch (Exception ex) {
            populateModel(model, "", "", ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/ListObjects")
    public String ListObjects(@RequestParam("bucket") String bucket, Model model) {
        try {
            if (bucket.isEmpty()) {
                populateModel(model, bucket, "", "Bucket parameter is missing.", List.of(), 500);
                return "s3";
            }

            HttpGet httpGet = new HttpGet(apiUrl + "/" + bucket);

            try (CloseableHttpResponse response = signingClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                String content = getContent(response.getEntity());
                populateModel(model, bucket, "", content, headers, statusCode);
            }
        } catch (Exception ex) {
            populateModel(model, bucket, "", ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/CreateBucket")
    public String createBucket(@RequestParam("bucket") String bucket, Model model) {
        try {
            if (bucket.isEmpty()) {
                populateModel(model, bucket, "", "Bucket parameter is missing.", List.of(), 500);
                return "s3";
            }
            bucket = encodeValue(bucket);
            HttpPut httpPut = new HttpPut(apiUrl + "/" + bucket);

            try (CloseableHttpResponse response = signingClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                populateModel(model, bucket, "", "", headers, statusCode);
            }

        } catch (Exception ex) {
            populateModel(model, bucket, "", ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/DeleteBucket")
    public String deleteBucket(@RequestParam("bucket") String bucket, Model model) {
        try {
            if (bucket.isEmpty()) {
                populateModel(model, bucket, "", "Bucket parameter is missing.", List.of(), 500);
                return "s3";
            }
            HttpDelete httpDelete = new HttpDelete(apiUrl + "/" + bucket);

            try (CloseableHttpResponse response = signingClient.execute(httpDelete)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                populateModel(model,bucket, "", "", headers, statusCode);
            }
        } catch (Exception ex) {
            populateModel(model, bucket, "", ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/UploadObject")
    public String uploadObject(@RequestParam("bucket") String bucket,
                               @RequestParam("file") MultipartFile file,
                               Model model) {

        if (bucket.isEmpty()) {
            populateModel(model, bucket, "", "Bucket parameter is missing.", List.of(), 500);
            return "s3";
        }

        if (file.isEmpty()) {
            populateModel(model, bucket, "", "File parameter is missing.", List.of(), 500);
            return "s3";
        }

        try {
            String object = file.getOriginalFilename();
            String encodedObject = encodeValue(object);
            HttpPut httpPut = new HttpPut(apiUrl + "/" + bucket + "/" + encodedObject);
            httpPut.setEntity(new ByteArrayEntity(file.getBytes(), ContentType.create(file.getContentType())));

            try (CloseableHttpResponse response = signingClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                populateModel(model, bucket, object, "", headers, statusCode);
            }

        } catch (Exception ex) {
            populateModel(model, bucket, file.getOriginalFilename(), ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/DownloadObject")
    public String uploadObject(@RequestParam("bucket") String bucket,
                               @RequestParam("object") String object,
                               Model model) {

        if (bucket.isEmpty()) {
            populateModel(model, bucket, object, "Bucket parameter is missing.", List.of(), 500);
            return "s3";
        }

        if (object.isEmpty()) {
            populateModel(model, bucket, object, "Object parameter is missing.", List.of(), 500);
            return "s3";
        }

        try {
            String encodedObject = encodeValue(object);
            HttpGet httpGet = new HttpGet(apiUrl + "/" + bucket + "/" + encodedObject);

            try (CloseableHttpResponse response = signingClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());

                HttpEntity httpEntity = response.getEntity();
                try (InputStream in = httpEntity.getContent()) {
                    // TODO: Currently converting object names with "/" to "_". Future: build dir structure based on "/"
                    String fileDownloadName = fileDownloadDir + "/" + object.replaceAll("/", "_");
                    FileOutputStream fos = new FileOutputStream(new File(fileDownloadName));
                    fos.write(in.readAllBytes());
                    fos.close();

                    String content = "File successfully saved to " + fileDownloadName;

                    populateModel(model, bucket, object, content, headers, statusCode);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            populateModel(model, bucket, object, ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @PostMapping("/DeleteObject")
    public String deleteObject(@RequestParam("bucket") String bucket,
                               @RequestParam("object") String object,
                               Model model) {
        if (bucket.isEmpty()) {
            populateModel(model, bucket, object, "Bucket parameter is missing.", List.of(), 500);
            return "s3";
        }

        if (object.isEmpty()) {
            populateModel(model, bucket, object, "Object parameter is missing.", List.of(), 500);
            return "s3";
        }

        try {
            String encodedObject = encodeValue(object);
            HttpDelete httpDelete = new HttpDelete(apiUrl + "/" + bucket + "/" +encodedObject);

            try (CloseableHttpResponse response = signingClient.execute(httpDelete)) {
                int statusCode = response.getStatusLine().getStatusCode();
                List<Header> headers = Arrays.stream(response.getAllHeaders()).collect(Collectors.toList());
                populateModel(model, bucket, object, "", headers, statusCode);
            }

        } catch (Exception ex) {
            populateModel(model, bucket, object, ex.getLocalizedMessage(), List.of(), 500);
        }

        return "s3";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    private String getContent(HttpEntity httpEntity)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {

        try (InputStream in = httpEntity.getContent()) {
            String contentType = httpEntity.getContentType().getValue();
            String allBytes = new String(in.readAllBytes());

            return contentType.equals("application/xml")
                    ? prettyPrint(toXmlDocument(allBytes))
                    : allBytes;
        }
    }

    private void populateModel(Model model, String bucket, String object, String xml, List<Header> headers, int statusCode) {
        model.addAttribute("bucket", bucket)
             .addAttribute("object", object)
             .addAttribute("response", xml)
             .addAttribute("headers", headers)
             .addAttribute("status", HttpStatus.valueOf(statusCode));
    }

    private String prettyPrint(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        DOMSource source = new DOMSource(document);
        StringWriter strWriter = new StringWriter();
        StreamResult result = new StreamResult(strWriter);

        transformer.transform(source, result);
        return strWriter.getBuffer().toString();
    }

    private Document toXmlDocument(String str) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(str)));
    }

    String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
}
