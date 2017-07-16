package com.liberty.book.crawler.calendar;

import com.liberty.book.crawler.common.DayBornResponse;
import com.liberty.book.crawler.common.RequestHelper;
import com.liberty.book.crawler.common.datamapper.DataMapper;
import com.liberty.book.crawler.entity.*;
import com.liberty.book.crawler.repository.*;
import com.liberty.book.crawler.service.TagService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 10.06.2017.
 */
@Service
public class CalendarCrawler {


    @Autowired
    private DataMapper mapper;

    @Autowired
    private AuthorBorndateRepository repository;

    @Autowired
    private AuthorRepository authorRepository;

    private String baseDomain = "https://www.livelib.ru/";
    private Calendar dateToParse = Calendar.getInstance();
    private Calendar endDate;



    public void crawl() {
        ArrayList<String> visitUrlList = new ArrayList<>();
        visitUrlList.add(baseDomain+"service/ff");
        visitUrlList.add(baseDomain+"service/vag");
        RequestHelper.setUrlList(visitUrlList);

    }

    public void init(){
        endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR,1);

    }

    public static void main(String[] args) {

        System.out.println(crawler.parseGiveawaysData(document));
    }


    private String loadAuthorsAtDateAndPage(String date, Integer page){
        CalendarCrawler crawler = new CalendarCrawler();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("page_no", page.toString()));
        params.add(new BasicNameValuePair("current_date", date));
        params.add(new BasicNameValuePair("is_new_design", "ll2015b"));
        crawler.init();
        return RequestHelper.executePostRequestAndGetResult("https://www.livelib.ru/author/born",params);
    }

    private Document loadAllAuthorsAtDate(String date){
        Integer page = 1;
        StringBuilder authors = new StringBuilder();
        Boolean isLastPage = false;
        while(!isLastPage) {
            String jsonDocument = loadAuthorsAtDateAndPage(date, page);
            DayBornResponse response = mapper.mapData(jsonDocument, DayBornResponse.class);
            authors.append(response.getContent());
            isLastPage = response.getEndData();
            page++;
        }
        return Jsoup.parse(authors.toString());
    }

    private void parseAndSaveAuthorsAtDate(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(calendar.getTime());
        Document authors = loadAllAuthorsAtDate(date);
        Elements authorList = authors.select("div.born-author");
        authorList.forEach(element -> {
            AuthorBorndateEntity entity = new AuthorBorndateEntity();
            Integer old = Integer.parseInt(element.select("span.born-age-year").attr("title"));
            Calendar bornDate =(Calendar) dateToParse.clone();
            bornDate.add(Calendar.YEAR,-old);
            entity.setBornYear(bornDate.get(Calendar.YEAR));
            entity.setBornMonth(bornDate.get(Calendar.MONTH));
            entity.setBornDay(bornDate.get(Calendar.DAY_OF_MONTH));

            entity.setAuthorName(element.select("div.born-author-title .born-author-td a").text());
            entity.setLivelibAuthorId(Long.parseLong(element.select("div.born-author-title .born-author-td a").attr("href")));
            findAndSetFlibustaAuthor(entity);
            repository.save(entity);
        });
        
    }

    private Long findAndSetFlibustaAuthor(AuthorBorndateEntity entity) {
            String[] split = entity.getAuthorName().split(" ");
            List<AuthorEntity> result;
            if (split.length == 1) {
                String lastName = split[0].toLowerCase();
                result = authorRepository.getByLastName(lastName);
            } else if (split.length == 2) {
                String firstName = split[0].toLowerCase();
                String lastName = split[1].toLowerCase();
                result = authorRepository.getByLastAndFistName(lastName,firstName);
            } else {
                String firstName = split[0].toLowerCase();
                String lastName = split[split.length - 1].toLowerCase();
                result = authorRepository.getByLastAndFistName(lastName,firstName);
            }
            System.out.println("Found : " + result.size() + " results for : " + entity.getAuthorName());
            if (CollectionUtils.isEmpty(result)) {
                System.err.println("Not found authors for : " + entity.getAuthorName());
            } else {
                AuthorEntity authorEntity = result.get(0);
                entity.setNeurolibAuthorId((long)authorEntity.getAuthorId());
                System.out.println("Selected " + authorEntity.getLastName()
                        + " with id : " + authorEntity.getAuthorId() + " for : " + entity.getAuthorName());
            }

    }



}
