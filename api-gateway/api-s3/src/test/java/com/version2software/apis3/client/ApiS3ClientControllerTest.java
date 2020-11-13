package com.version2software.apis3.client;

import com.amazonaws.util.StringInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * For integration test. The webserver is not started, but the API Gateway is invoked.
 */
@WebMvcTest(ApiS3ClientController.class)
class ApiS3ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloseableHttpClient signingClient;

    @Mock
    private CloseableHttpResponse closeableHttpResponse;

    @Mock
    private StatusLine statusLine;

    @Mock
    private HttpEntity httpEntity;

    @Test
    public void getOK() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ListAllBuckets")))
                .andExpect(content().string(containsString("List all buckets")));
    }

    @Test
    public void listBuckets() throws Exception {

        when(signingClient.execute(any())).thenReturn(closeableHttpResponse);
        when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(closeableHttpResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContentType()).thenReturn(new BasicHeader("Content-Type", "plain/text"));
        when(httpEntity.getContent()).thenReturn(new StringInputStream("<ListAllMyBucketsResult></ListAllMyBucketsResult>"));

        mockMvc.perform(post("/ListAllBuckets")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("200 OK")))
                .andExpect(content().string(containsString("ListAllMyBucketsResult")));
    }

    @Test
    public void listBucketsNegative() throws Exception {
        when(signingClient.execute(any())).thenThrow(new IOException());

        mockMvc.perform(post("/ListAllBuckets")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk()) // OK because we caught and handled the exception ourselves
                .andExpect(content().string(containsString("500 INTERNAL_SERVER_ERROR")));
    }

    @Test
    public void deleteObject() throws Exception {

        when(signingClient.execute(any())).thenReturn(closeableHttpResponse);
        when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(closeableHttpResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new StringInputStream(""));

        mockMvc.perform(post("/DeleteObject")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("bucket", "thing"),
                        new BasicNameValuePair("object", "stuff/stuff"))))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("200 OK")))
                .andExpect(content().string(containsString("stuff/stuff")));
    }

    @Test
    public void encodeValue() throws Exception {
        String encodedValue = new ApiS3ClientController(null).encodeValue("a/b");
        assertEquals("a%2Fb", encodedValue);
    }
}