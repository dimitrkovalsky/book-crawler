package com.liberty.book.crawler.main;

import com.liberty.book.crawler.config.AppConfig;
import com.liberty.book.crawler.selections.SelectionsCrawler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by user on 15.06.2017.
 */
public class SelectionsRunner {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        SelectionsCrawler crawler = ctx.getBean(SelectionsCrawler.class);
       // crawler.crawl();
        crawler.mapToNeurolib();
    }
}
