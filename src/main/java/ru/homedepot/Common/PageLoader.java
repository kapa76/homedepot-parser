package ru.homedepot.Common;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class PageLoader {
    private static String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru-RU; rv:1.9.1.4) Gecko/20091016 Firefox/3.5.4 (.NET CLR 3.5.30729)";

    public static String Loader(String url) {
        HttpClient
                client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(10000)
                        .setConnectTimeout(5000)
                        .setExpectContinueEnabled(false)
                        .setSocketTimeout(5000)
                        .setCookieSpec("easy")
                        .build())
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(100)
                .build();

        HttpGet httpGet = new HttpGet(url);
        String body = "";
        try {
            httpGet.setHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException exception) {
            System.out.println("Exception PageLoader: can't load page: " + exception.getMessage());
        }

        return body;
    }

    public static String LoaderPost(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        String body = "";
        try {
            httpPost.addHeader("User-Agent", USER_AGENT);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            ArrayList<NameValuePair> postParameters;
            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("active", "prilet"));
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

            HttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException exception) {
            System.out.println("PageLoader: can't load page: " + exception.getMessage());
        }
        return body;
    }
}
