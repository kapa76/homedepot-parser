package ru.homedepot.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PageLoader {

    protected static final Logger logger = LoggerFactory.getLogger(PageLoader.class);

    private static String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru-RU; rv:1.9.1.4) Gecko/20091016 Firefox/3.5.4 (.NET CLR 3.5.30729)";

    private static long bytesTransferred = 0;
    private static long requestPageLoader = 0;
    private static List<Long> requestTimeExec = new ArrayList<>();

    private static HttpClient client = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setExpectContinueEnabled(false)
                    .setCookieSpec("easy")
                    .build())
            .setMaxConnPerRoute(20)
            .setMaxConnTotal(100)
            .build();

    public static String Loader(String url) {
        HttpGet httpGet = new HttpGet(url);
        String body = "";
        try {
            httpGet.setHeader("User-Agent", USER_AGENT);

            long startTime = System.currentTimeMillis();

            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            long endTime = System.currentTimeMillis();
            requestPageLoader++;
            requestTimeExec.add(endTime - startTime);

            if (statusCode == 200) {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
                addTransferredBytes(body);
            }
        } catch (IOException exception) {
            logger.debug("Exception PageLoader: can't load page: " + exception.getMessage());
        }

        return body;
    }

    private static void addTransferredBytes(String body) {
        bytesTransferred += body.length();
    }

    public static long getRequestPageLoader() {
        return requestPageLoader;
    }

    public static long getBytesTransferred() {
        return bytesTransferred;
    }

    public static List<Long> getRequestTimeExec() {
        return requestTimeExec;
    }
}
