package com.sky.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class HttpClientTest {

    /**
     * 测试httpclient发送GET方式的请求
     */
    @Test
    public void testget() throws IOException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建HttpGet对象，设置url访问地址
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        //发送GET请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //获取响应结果状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("状态码为：" + statusCode);

        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        System.out.println("服务端返回的数据为：" + result);

        //关闭资源
        response.close();
        httpClient.close();
    }


    /**
     * 测试httpclient发送POST方式的请求
     */
    @Test
    public void testpost() throws IOException, JSONException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建HttpGet对象，设置url访问地址
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");

        StringEntity entity = new StringEntity(jsonObject.toString());
        //指定请求的编码方式
        entity.setContentEncoding("utf-8");
        //指定数据格式(JSON)
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        //发送GET请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        //获取响应结果状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("状态码为：" + statusCode);

        HttpEntity entity1 = response.getEntity();
        String result = EntityUtils.toString(entity1);
        System.out.println("服务端返回的数据为：" + result);

        //关闭资源
        response.close();
        httpClient.close();
    }
}
