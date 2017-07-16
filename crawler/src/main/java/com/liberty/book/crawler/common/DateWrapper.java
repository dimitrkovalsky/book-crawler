package com.liberty.book.crawler.common;

import com.liberty.book.crawler.config.AppConfig;
import com.liberty.book.crawler.crawler4j.BookCrawler;
import com.liberty.book.crawler.repository.AuthorBorndateRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Calendar;

/**
 * Created by user on 02.06.2017.
 */
public class DateWrapper {
    public static void main(String[] args) throws Exception{
        DateWrapper wrapper = new DateWrapper();

        wrapper.makeItEveryDay();
    }

    private Calendar currentDay = Calendar.getInstance();
    private Calendar stopDay = Calendar.getInstance();


    DateWrapper(){
        stopDay.add(Calendar.YEAR,1);
    }

    public Calendar getCurrentDay() {
        return currentDay;
    }

    public Calendar getStopDay() {
        return stopDay;
    }

    public void nextDay(){
        currentDay.add(Calendar.HOUR,24);
    }
    public boolean doSomething(Calendar calendar){
        PageWrapper wrapper = new PageWrapper(calendar);
        wrapper.makeItEveryPage();
        return true;
    }

    private void makeItEveryDay(){
        while (!currentDay.after(stopDay)){
            if(doSomething(currentDay)){
                nextDay();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
