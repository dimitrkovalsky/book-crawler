package com.liberty.book.crawler.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Dmytro_Kovalskyi on 17.02.2016.
 */
@Slf4j
public class RequestHelper {

    private static CloseableHttpClient httpClient;
    private static List<String> urlList = new ArrayList<>();
    private static Date nextCookieLoad;
    private static List<Header> additionHeaders;

    public static void setUrlList(List<String> urlList) {
        RequestHelper.urlList = urlList;
    }

    public static void setAdditionalHeaders(List<Header> headers) {
        RequestHelper.additionHeaders = headers;
    }


    public static InputStream executeRequest(String url) {
        try {
            prepareClient();
            HttpGet request = new HttpGet(url);
            request.setHeaders(getBaseHeaders());
            HttpResponse response = httpClient.execute(request);

            HttpEntity responseEntity = response.getEntity();
            getCookiesFrom(url);
            return responseEntity.getContent();
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }

    public static InputStream executePostRequest(String url, List<NameValuePair> params) {
        try {
            prepareClient();
            HttpPost request = new HttpPost(url);
            request.setHeaders(getBaseHeaders());
            request.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            getCookiesFrom(url);
            return responseEntity.getContent();
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }

    public static HttpEntity executePostRequestEntity(String url, List<NameValuePair> params) {
        try {
            prepareClient();
            HttpPost request = new HttpPost(url);
            //request.setHeaders(getBaseHeaders());
            request.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            getCookiesFrom(url);
            return responseEntity;
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }

    public static void executeRequestAndShowResult(String url) {
        System.out.println(executeRequestAndGetResult(url));
    }

    public static String executeRequestAndGetResult(String url) {
        return readResult(executeRequest(url));
    }

    public static String executePostRequestAndGetResult(String url,List<NameValuePair> params) {
        return readResult(executePostRequest(url,params));
    }
    public static String executePostRequestAndGetResultGoogle(String url,List<NameValuePair> params) {
        return readResult(executePostRequestEntity(url,params));
    }

    public static String readResult(InputStream inputStream) {
        try {
            if (inputStream == null) {
                return "";
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                return br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            log.error("Content unavailable for : " + inputStream);
            return "";
        }
    }

    public static String readResult(HttpEntity entity) {
        String response="";
        try{
            response = EntityUtils.toString(entity);
        }catch (IOException e){}

        return response;

    }

    public static void saveToFile(String fileName, String data) {
        try {
            Path targetPath = new File(fileName).toPath();
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.write(targetPath, data.getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void saveToFile(String fileName, InputStream stream) throws IOException {
        Path targetPath = new File(fileName).toPath();
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        stream.close();
    }

    private static void prepareClient(){
        if (httpClient==null){
            httpClient = HttpClients.createMinimal();
        }
    }

    private static void getCookiesFrom(String refererUrl){
        Date currentTime = new Date();
        if(nextCookieLoad==null||nextCookieLoad.before(currentTime))
        for (String url:urlList) {
            getCookieFrom(url,refererUrl);
        }

        nextCookieLoad = new Date(new Date().getTime()+3600000);
    }


    private static void getCookieFrom(String url,String refererUrl){
        try {
            HttpGet request = new HttpGet(url);
            request.setHeaders(getBaseHeaders());
            request.setHeader("Referer",refererUrl);
            HttpResponse response = httpClient.execute(request);
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    private static Header[] getBaseHeaders(){
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept","text/javascript, text/html, application/xml, text/xml, */*"));
        headers.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
        headers.add(new BasicHeader("Accept-Encoding","gzip, deflate, br"));
        headers.add(new BasicHeader("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4"));
        headers.add(new BasicHeader("User-Agent","User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
        headers.add(new BasicHeader("Cache-Control","no-cache"));

        if(additionHeaders!=null){
            headers.addAll(additionHeaders);
        }
        Header[] stockArr = new Header[headers.size()];

        return headers.toArray(stockArr);
    }
}
