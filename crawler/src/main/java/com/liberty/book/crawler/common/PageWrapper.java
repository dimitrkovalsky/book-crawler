package com.liberty.book.crawler.common;

import com.liberty.book.crawler.common.datamapper.JacksonDataMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 02.06.2017.
 */
public class PageWrapper {

    public static void main(String[] args) {
        PageWrapper wrapper = new PageWrapper(Calendar.getInstance());
        wrapper.makeItEveryPage();
}

    private Integer currentPage = 0;

    private Calendar currentDay;

    PageWrapper(Calendar calendar){
        this.currentDay = calendar;
    }

    DayBornResponse parseResponse(String entity){
        System.out.println(entity);
        JacksonDataMapper mapper = new JacksonDataMapper();
       return mapper.mapData(entity, DayBornResponse.class);
    }

    public void processData(DayBornResponse response){

        BornDayParser bornDayParser  =new BornDayParser(currentDay);
        System.out.println(bornDayParser.process(response.getContent()));
    }

    void makeItEveryPage(){
        String entity = loadPage();
        DayBornResponse response = parseResponse(entity);
        processData(response);

        if(!response.getEndData()){
            currentPage++;
            makeItEveryPage();
        }
    }

    String loadPage(){
        try (CloseableHttpClient httpclient = HttpClients.createSystem();){
        HttpPost httpPost = new HttpPost("http://www.livelib.ru/author/born");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("page_no", currentPage.toString()));
        nvps.add(new BasicNameValuePair("current_date", "2017-06-04"));
        nvps.add(new BasicNameValuePair("is_new_design", "ll2015b"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("X-Requested-With","XMLHttpRequest"));
            headers.add(new BasicHeader("Accept","text/javascript, text/html, application/xml, text/xml, */*"));
            headers.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
            headers.add(new BasicHeader("Referer","http://www.livelib.ru/authors"));
            Header stockArr[] = new Header[headers.size()];

        httpPost.setHeaders(headers.toArray(stockArr));

        CloseableHttpResponse response2 = httpclient.execute(httpPost);
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
