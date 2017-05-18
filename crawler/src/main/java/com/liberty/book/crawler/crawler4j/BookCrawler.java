package com.liberty.book.crawler.crawler4j;

import com.google.common.io.Files;
import com.liberty.book.crawler.entity.AuthorImageEntity;
import com.liberty.book.crawler.entity.BookImageEntity;
import com.liberty.book.crawler.repository.AuthorImageRepository;
import com.liberty.book.crawler.repository.BookImageRepository;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import java.util.regex.Pattern;

/**
 * Created by user on 20.04.2017.
 */
@Service
@Scope("prototype")
@Slf4j
public class BookCrawler extends WebCrawler {

    @Autowired
    private AuthorImageRepository authorImageRepository;

    @Autowired
    private BookImageRepository bookImageRepository;

    private static File storageFolder;

    static {
        storageFolder = new File("C:/Users/user/IdeaProjects/book-crawler/dump/img");
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }


    private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");

    private static Pattern FILE_ENDING_EXCLUSION_PATTERN = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g" +
            "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
            "|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx" +
            "|swf" +
            "|zip|rar|gz|bz2|7z|bin" +
            "|xml|txt|java|c|cpp|exe" +
            "))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {

        String href = url.getURL().toLowerCase();
        return !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches()
                &&url.getURL().contains("flisland.net")
                &&!url.getURL().contains("node")
                &&!url.getURL().contains("read")
                &&!url.getURL().contains("comment")
                &&!url.getURL().contains("forum")
                &&!url.getURL().contains("polka")
                &&!url.getURL().contains("blog")
                &&!url.getURL().contains("rec")
                &&!url.getURL().contains("/mobi")
                &&!url.getURL().contains("/epub")
                &&!url.getURL().contains("/fb2")
                &&!url.getURL().contains("mobile")
                &&!url.getURL().contains("/rss")
                &&!url.getURL().contains("/edit")
                &&!url.getURL().contains("/stat/")
                &&!url.getURL().contains("/download");
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);


        // We are only interested in processing images which are bigger than 1k
        if (!imgPatterns.matcher(url).matches() ||
                !((page.getParseData() instanceof BinaryParseData) ||
                        (page.getContentData().length < (1 * 1024)))) {
            return;
        }

        // get a unique name for storing this image
        String picturePath = page.getWebURL().getPath();

        // store image
        try {
            File file = new File(storageFolder.getAbsolutePath()+picturePath);
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            Files.write(page.getContentData(), file);
            String id = parentDir.getName();

            File nidDir = parentDir.getParentFile();
            String nid = nidDir.getName();

            File typeDir = nidDir.getParentFile();
            String type = typeDir.getName();

            if ("i".equals(type)||"ib".equals(type)){
                BookImageEntity imageEntity = new BookImageEntity();
                imageEntity.setBookId(Long.parseLong(id));
                imageEntity.setNid(Integer.parseInt(nid));
                imageEntity.setFile(picturePath.replace("/i/","/").replace("/ib/","/"));
                bookImageRepository.save(imageEntity);
            }

            if ("ia".equals(type)){
                AuthorImageEntity imageEntity = new AuthorImageEntity();
                imageEntity.setAuthorId(Long.parseLong(id));
                imageEntity.setNid(Integer.parseInt(nid));
                imageEntity.setFile(picturePath.replace("/ia/","/"));
                authorImageRepository.save(imageEntity);
            }


            logger.info("Stored: {}", url);
        } catch (IOException iox) {
            logger.error("Failed to write file: " + picturePath, iox);
        }

    }


}
