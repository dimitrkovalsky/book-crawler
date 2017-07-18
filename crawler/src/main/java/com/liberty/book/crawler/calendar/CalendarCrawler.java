package com.liberty.book.crawler.calendar;

import com.liberty.book.crawler.common.DayBornResponse;
import com.liberty.book.crawler.common.RequestHelper;
import com.liberty.book.crawler.common.datamapper.DataMapper;
import com.liberty.book.crawler.entity.*;
import com.liberty.book.crawler.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private SynonymNameRepository synonymNameRepository;

    private String baseDomain = "https://www.livelib.ru/";
    private Calendar dateToParse = Calendar.getInstance();
    private Calendar endDate;



    public void crawl() {
        init();
        ArrayList<String> visitUrlList = new ArrayList<>();
        visitUrlList.add(baseDomain+"service/ff");
        visitUrlList.add(baseDomain+"service/vag");
        RequestHelper.setUrlList(visitUrlList);
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("X-Requested-With","XMLHttpRequest"));
        RequestHelper.setAdditionalHeaders(headers);

        parseWholeYear();
    }

    public void init(){
        endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR,1);

    }

    public static void main(String[] args) {
    }


    private String loadAuthorsAtDateAndPage(String date, Integer page){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("page_no", page.toString()));
        params.add(new BasicNameValuePair("current_date", date));
        params.add(new BasicNameValuePair("is_new_design", "ll2015b"));
        return RequestHelper.executePostRequestAndGetResult(baseDomain+"author/born",params);
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
            String oldString = element.select("span.born-age-year").attr("title");
            if("".equals(oldString)){
                entity.setBornMonth(dateToParse.get(Calendar.MONTH) + 1);
                entity.setBornDay(dateToParse.get(Calendar.DAY_OF_MONTH));
            }else {
                Integer old = Integer.parseInt(oldString);
                Calendar bornDate = (Calendar) dateToParse.clone();
                bornDate.add(Calendar.YEAR, - old);
                entity.setBornYear(bornDate.get(Calendar.YEAR));
                entity.setBornMonth(bornDate.get(Calendar.MONTH) + 1);
                entity.setBornDay(bornDate.get(Calendar.DAY_OF_MONTH));
            }
            entity.setAuthorName(element.select("div.born-author-title .born-author-td a").text());
            entity.setLivelibAuthorId(Long.parseLong(element.select("div.born-author-title .born-author-td a").attr("href").replace("/author/","")));
            findAndSetFlibustaAuthor(entity);
            repository.save(entity);
        });
        
    }

    private void parseWholeYear(){
        while(dateToParse.before(endDate)){
            parseAndSaveAuthorsAtDate(dateToParse);
            dateToParse.add(Calendar.HOUR,24);
            try {
                TimeUnit.SECONDS.sleep(5);

            }catch (InterruptedException e){}
        }
    }


    private void findAndSetFlibustaAuthor(AuthorBorndateEntity entity) {
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

    private void findAndSetFlibustaAuthorWithSynonym(AuthorBorndateEntity entity) {
        String[] split = entity.getAuthorName().split(" ");
        List<AuthorEntity> result;
        if (split.length == 1) {
            String lastName = split[0].toLowerCase();
            result = authorRepository.getByLastName(lastName);
        } else if (split.length == 2) {
            String firstName = split[0].toLowerCase();
            String lastName = split[1].toLowerCase();
            result = authorRepository.getByLastAndFistName(lastName,firstName);
            if(CollectionUtils.isEmpty(result)){
                List<String> names = findSynonyms(firstName);
                for (String name : names){
                    result = authorRepository.getByLastAndFistName(lastName,name);
                    if(!CollectionUtils.isEmpty(result)){
                        break;
                    }
                }
            }
        } else {
            String firstName = split[0].toLowerCase();
            String lastName = split[split.length - 1].toLowerCase();
            result = authorRepository.getByLastAndFistName(lastName,firstName);
            if(CollectionUtils.isEmpty(result)){
                List<String> names = findSynonyms(firstName);
                for (String name : names){
                    result = authorRepository.getByLastAndFistName(lastName,name);
                    if(!CollectionUtils.isEmpty(result)){
                        break;
                    }
                }
            }
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

    private void findAndSetFlibustaAuthorWithTranslation(AuthorBorndateEntity entity) {
        String translatedName = translateName(entity.getAuthorName());
        String[] split = translatedName.split(" ");
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

    private List<String> findSynonyms(String name){
        SynonymNameEntity nameEntity = synonymNameRepository.findFirstByNameEquals(name);
        List<String> synonymList;
        if(nameEntity==null){
            synonymList = new ArrayList<>();
            synonymList.add(name);
        }else{
            List<SynonymNameEntity> nameEntities = synonymNameRepository.findAllByNameId(nameEntity.getNameId());
            synonymList = nameEntities.stream().map(synonymNameEntity -> {return synonymNameEntity.getName();}).collect(Collectors.toList());
        }
        return synonymList;
    }

    private String loadSynonymNames(){
        return RequestHelper.executeRequestAndGetResult("https://raw.githubusercontent.com/zhuharev/synonym_name.txt/master/synonym_name.txt");
    }

    private List<String> parseLines(String text){
        long start = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException exc) {
            // quit
        }
        return result;
    }

    public void loadSynonymToDatabase(){
        String synonymNames = loadSynonymNames();
        synonymNames = replaceDashToComma(synonymNames);
        List<String>nameLines = parseLines(synonymNames);
        Integer nameNumber = 1;
        for(String names : nameLines){
            String[] nameArray = names.split(",");
            for(String name:nameArray){
                String cleanName = clearName(name);
                SynonymNameEntity synonymNameEntity = new SynonymNameEntity();
                synonymNameEntity.setName(cleanName);
                synonymNameEntity.setNameId((long)nameNumber);
                synonymNameRepository.save(synonymNameEntity);
            }
            nameNumber++;
            System.out.println(names);
        }

    }

    private String replaceDashToComma(String text){
        return text.replace(" - ",", ");
    }

    private String clearName(String name){
        return name.replace(" ","");
    }

    public void remapWithSynonyms(){
        List<AuthorBorndateEntity> entities = repository.findAllByNeurolibAuthorIdIsNull();
        for (AuthorBorndateEntity entity:entities){
            findAndSetFlibustaAuthorWithSynonym(entity);
            repository.save(entity);
        }
    }

    public void remapWithTranslate(){
        List<AuthorBorndateEntity> entities = repository.findAllByNeurolibAuthorIdIsNull();
        for (AuthorBorndateEntity entity:entities){
            findAndSetFlibustaAuthorWithTranslation(entity);
            repository.save(entity);
        }
    }



    private String translateName(String name){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", name));
        params.add(new BasicNameValuePair("target", "en"));
        params.add(new BasicNameValuePair("source", "ru"));
        params.add(new BasicNameValuePair("format", "text"));
        params.add(new BasicNameValuePair("key", "AIzaSyAjLb5mCuEGckU_shCm-d4wlYvZZbZ1PuQ"));
        String result = RequestHelper.executePostRequestAndGetResultGoogle("https://translation.googleapis.com/language/translate/v2",params);

        return result.split("\"translatedText\": \"")[1].split("\"")[0];
    }

}
