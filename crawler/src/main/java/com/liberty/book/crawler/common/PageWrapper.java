package com.liberty.book.crawler.common;

import com.liberty.book.crawler.common.datamapper.JacksonDataMapper;
import com.liberty.book.crawler.config.AppConfig;
import com.liberty.book.crawler.entity.AuthorBorndateEntity;
import com.liberty.book.crawler.repository.AuthorBorndateRepository;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 02.06.2017.
 */
public class PageWrapper {

    public static void main(String[] args) {
        PageWrapper wrapper = new PageWrapper(Calendar.getInstance());
        wrapper.makeItEveryPage();
}

    ApplicationContext ctx =
            new AnnotationConfigApplicationContext(AppConfig.class);
    AuthorBorndateRepository repository = (AuthorBorndateRepository) ctx.getBean("authorBorndateRepository");

    private CloseableHttpClient httpclient;
    private Integer currentPage = 1;

    private Calendar currentDay;

    PageWrapper(Calendar calendar){
        this.currentDay = calendar;
    }

    DayBornResponse parseResponse(String entity){
        JacksonDataMapper mapper = new JacksonDataMapper();
       return mapper.mapData(entity, DayBornResponse.class);
    }

    public void processData(DayBornResponse response){

        BornDayParser bornDayParser  = new BornDayParser(currentDay);
        List<DayBornObject> dayBornObjects = bornDayParser.process(response.getContent());
        List<AuthorBorndateEntity> entities = dayBornObjects.stream().map(object -> {return object.mapToEntity();}).collect(Collectors.toList());
        repository.save(entities);
    }

    void makeItEveryPage(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String entity = loadPage();
        DayBornResponse response = parseResponse(entity);
        processData(response);

        if(!response.getEndData()){
            currentPage++;
            makeItEveryPage();
        }
    }

    void init(){
        if (httpclient==null){
            httpclient = HttpClients.createSystem();
        }

        HttpGet httpGet = new HttpGet("https://www.livelib.ru/author/");

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept","text/javascript, text/html, application/xml, text/xml, */*"));
        headers.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
        headers.add(new BasicHeader("Referer","https://www.livelib.ru/authors"));
        headers.add(new BasicHeader("Accept-Encoding","gzip, deflate, br"));
        headers.add(new BasicHeader("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4"));
        headers.add(new BasicHeader("User-Agent","User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
        headers.add(new BasicHeader("Cache-Control","no-cache"));
        Header stockArr[] = new Header[headers.size()];

        httpGet.setHeaders(headers.toArray(stockArr));

        try {
            CloseableHttpResponse response2 = httpclient.execute(httpGet);
            HttpEntity entity2 = response2.getEntity();
            EntityUtils.consume(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        }

         httpGet = new HttpGet("https://www.livelib.ru/service/ff");
        httpGet.setHeaders(headers.toArray(stockArr));

        try {
            CloseableHttpResponse response2 = httpclient.execute(httpGet);
            HttpEntity entity2 = response2.getEntity();
            EntityUtils.consume(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    String loadPage(){
        try {
            init();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(currentDay.getTime());
            System.out.println(date);
            HttpPost httpPost = new HttpPost("https://www.livelib.ru/author/born");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("page_no", currentPage.toString()));
            nvps.add(new BasicNameValuePair("current_date", date));
            nvps.add(new BasicNameValuePair("is_new_design", "ll2015b"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("X-Requested-With","XMLHttpRequest"));
            headers.add(new BasicHeader("Accept","text/javascript, text/html, application/xml, text/xml, */*"));
            headers.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
            headers.add(new BasicHeader("Referer","https://www.livelib.ru/authors"));
            headers.add(new BasicHeader("Accept-Encoding","gzip, deflate, br"));
            headers.add(new BasicHeader("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4"));
            headers.add(new BasicHeader("User-Agent","User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
            headers.add(new BasicHeader("Cache-Control","no-cache"));
            Header stockArr[] = new Header[headers.size()];

        httpPost.setHeaders(headers.toArray(stockArr));

        CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println(response2.getFirstHeader("Location"));
        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            String responseXml = EntityUtils.toString(entity2);
            EntityUtils.consume(entity2);
            return responseXml;
        } finally {
            response2.close();
        }

        }catch (Exception e){}
    return null;
    }
}
