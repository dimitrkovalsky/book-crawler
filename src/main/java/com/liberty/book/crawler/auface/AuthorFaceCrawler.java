package com.liberty.book.crawler.auface;

import com.liberty.book.crawler.common.LivelibRequestHelper;
import com.liberty.book.crawler.entity.AuthorEntity;
import com.liberty.book.crawler.entity.AuthorFaceLivelibEntity;
import com.liberty.book.crawler.entity.AuthorImageEntity;
import com.liberty.book.crawler.repository.AuthorFaceLivelibRepository;
import com.liberty.book.crawler.repository.AuthorImageRepository;
import com.liberty.book.crawler.repository.AuthorRepository;
import com.liberty.book.crawler.service.ImageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27.08.2017.
 */
@Component
public class AuthorFaceCrawler {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorFaceLivelibRepository authorFaceLivelibRepository;

    @Autowired
    private AuthorImageRepository authorImageRepository;

    @Autowired
    private ImageService imageService;

    private String baseDomain = "https://www.livelib.ru/";


    public void crawl() {
        ArrayList<String> visitUrlList = new ArrayList<>();
        visitUrlList.add(baseDomain+"service/ff");
        visitUrlList.add(baseDomain+"service/vag");
        LivelibRequestHelper.setUrlList(visitUrlList);
        crawlAuthors();
        removeGroupAuthors();
        mapAuthorsByAltName();
        remapWithTranslate();
        mapAuthorsWithShortName();
        downloadAndSaveFaces();
    }

    public void removeGroupAuthors(){
        authorFaceLivelibRepository.deleteAllByAuthorNameContaining(",");
        authorFaceLivelibRepository.deleteAllByAuthorNameContaining("&");
    }


    public void downloadAndSaveFaces(){
        List<AuthorFaceLivelibEntity> authorEntities = authorFaceLivelibRepository.getEntityToDownloadFace();
        authorEntities.parallelStream().forEach(authorFaceLivelibEntity -> {

            Long neurolibId = authorFaceLivelibEntity.getNeurolibAuthorId();
            String link = authorFaceLivelibEntity.getFaceUrl().replace("100x100","200");

            String path = imageService.loadAndSaveImage(link,neurolibId.toString());
            if(path!=null){
                AuthorImageEntity authorImageEntity = new AuthorImageEntity();
                authorImageEntity.setFile(path);
                authorImageEntity.setAuthorId(neurolibId);
                authorImageEntity.setNid(100);

               authorImageRepository.save(authorImageEntity);
            }
        });
    }


    public void crawlAuthors(){
        String retrieved = LivelibRequestHelper.executeRequestAndGetResult("https://www.livelib.ru/authors/popular");
        Document document = Jsoup.parse(retrieved);
        String nextPageLink = getNextPageLink(document);
        while (nextPageLink!=null) {
            retrieved = LivelibRequestHelper.executeRequestAndGetResult(baseDomain+nextPageLink);
            document = Jsoup.parse(retrieved);
            List<AuthorFaceLivelibEntity> authorEntities = parseAuthorList(document);
            authorEntities.parallelStream().forEach(authorFaceLivelibEntity -> {
                findAndSetFlibustaAuthor(authorFaceLivelibEntity);
                authorFaceLivelibRepository.save(authorFaceLivelibEntity);
            });
            nextPageLink = getNextPageLink(document);
        }
    }

    public void mapAuthorsByAltName(){
        List<AuthorFaceLivelibEntity> authorEntities = authorFaceLivelibRepository.findAllByAuthorAltNotAndNeurolibAuthorIdIsNull("");
        authorEntities.parallelStream().forEach(authorFaceLivelibEntity -> {
            findAndSetFlibustaAuthorAlt(authorFaceLivelibEntity);
            authorFaceLivelibRepository.save(authorFaceLivelibEntity);
        });
    }

    public void mapAuthorsWithShortName(){
        List<AuthorFaceLivelibEntity> authorEntities = authorFaceLivelibRepository.findAllByNeurolibAuthorIdIsNull();
        authorEntities.parallelStream().forEach(authorFaceLivelibEntity -> {
            findAndSetFlibustaAuthorByShort(authorFaceLivelibEntity);
            authorFaceLivelibRepository.save(authorFaceLivelibEntity);
        });
    }


    public void remapWithTranslate(){
        List<AuthorFaceLivelibEntity> entities = authorFaceLivelibRepository.findAllByNeurolibAuthorIdIsNull();
        entities.parallelStream().forEach(authorFaceLivelibEntity -> {
            findAndSetFlibustaAuthorWithTranslation(authorFaceLivelibEntity);
            authorFaceLivelibRepository.save(authorFaceLivelibEntity);
        });
    }

    private String getNextPageLink(Document document){
        String link = document.select(".i-pager-next").parents().attr("href");
        if ("".equals(link)){
            return null;
        }
        else return link;
    }

    private String getNextPageLinkFromElements(Elements elements){
        String link = elements.select(".i-pager-next").parents().attr("href");
        if ("".equals(link)
                ||link.contains("/comments/")){
            return null;
        }
        else return link;
    }

    private List<AuthorFaceLivelibEntity> parseAuthorList(Document document){
        List<AuthorFaceLivelibEntity> authorList = new ArrayList<>();
        Elements authors = document.select("div.block-author").select("div.event-author");
        authors.forEach(element -> {
            AuthorFaceLivelibEntity entity = new AuthorFaceLivelibEntity();
            entity.setAuthorName(element.select("a.block-book-title").text());
            entity.setAuthorAlt(element.select("span.author-original").text().replace("(","").replace(")",""));
            entity.setFaceUrl(element.select("span.boocover").attr("style").split("\\(")[1].replace(")",""));
            String id = element.select("a.block-book-title").attr("href").replace("/author/","").split("-")[0];
            entity.setLivelibAuthorId(Long.parseLong(id));
            authorList.add(entity);
        });

        System.out.println("Fetched links:"+authorList.size());
        return authorList;
    }

    private void findAndSetFlibustaAuthor(AuthorFaceLivelibEntity entity) {
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

    private void findAndSetFlibustaAuthorAlt(AuthorFaceLivelibEntity entity) {
        String[] split = entity.getAuthorAlt().split(" ");
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

    private void findAndSetFlibustaAuthorByShort(AuthorFaceLivelibEntity entity) {
        String[] split = entity.getAuthorName().split(" ");
        List<AuthorEntity> result;
        if (split.length == 1) {
            String lastName = split[0].toLowerCase();
            result = authorRepository.getByLastName(lastName);
        } else if (split.length == 2) {
            String firstName = split[0].toLowerCase();
            firstName = makeShortName(firstName);
            String lastName = split[1].toLowerCase();
            result = authorRepository.getByLastAndFistName(lastName,firstName);
        } else {
            String firstName = split[0].toLowerCase();
            firstName = makeShortName(firstName);
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

    private String makeShortName(String name){
        return name.charAt(0)+".";
    }




    private void findAndSetFlibustaAuthorWithTranslation(AuthorFaceLivelibEntity entity) {
        String translatedName = entity.getAuthorName();
        try {
            translatedName = translateName(entity.getAuthorName());
        }catch (Exception e){}
        System.out.println(entity.getAuthorName()+" -> "+translatedName);
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

    private String translateName(String name){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", name));
        params.add(new BasicNameValuePair("target", "en"));
        params.add(new BasicNameValuePair("source", "ru"));
        params.add(new BasicNameValuePair("format", "text"));
        params.add(new BasicNameValuePair("model", "base"));
        params.add(new BasicNameValuePair("key", "AIzaSyAjLb5mCuEGckU_shCm-d4wlYvZZbZ1PuQ"));
        String result = LivelibRequestHelper.executePostRequestAndGetResultGoogle("https://translation.googleapis.com/language/translate/v2",params);

        return result.split("\"translatedText\": \"")[1].split("\"")[0];
    }


}
