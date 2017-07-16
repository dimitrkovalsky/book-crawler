package com.liberty.book.crawler.main;

import com.liberty.book.crawler.calendar.CalendarCrawler;
import com.liberty.book.crawler.config.AppConfig;
import com.liberty.book.crawler.quotes.QuotesCrawler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * User: Dimitr
 * Date: 02.06.2017
 * Time: 8:13
 */
@Configuration
public class CalendarRunner {


    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        CalendarCrawler crawler = ctx.getBean(CalendarCrawler.class);
        crawler.crawl();
    }
}
