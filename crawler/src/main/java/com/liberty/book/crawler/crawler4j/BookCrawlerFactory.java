package com.liberty.book.crawler.crawler4j;

import com.liberty.book.crawler.config.AppConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * Created by user on 21.04.2017.
 */
public class BookCrawlerFactory implements CrawlController.WebCrawlerFactory<WebCrawler> {

    private ApplicationContext ctx =
            new AnnotationConfigApplicationContext(AppConfig.class);

    @Override
    public WebCrawler newInstance() {
        BookCrawler crawler = (BookCrawler) ctx.getBean("bookCrawler");
        return crawler;

    }
}
