package com.liberty.book.crawler.main;

import com.liberty.book.crawler.auface.AuthorFaceCrawler;
import com.liberty.book.crawler.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * User: Dimitr
 * Date: 02.06.2017
 * Time: 8:13
 */
@Configuration
public class AuthorFaceRunner {


    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        AuthorFaceCrawler crawler = ctx.getBean(AuthorFaceCrawler.class);
        crawler.crawl();
        //crawler.mapToFlibustaAuthors();
    }
}
