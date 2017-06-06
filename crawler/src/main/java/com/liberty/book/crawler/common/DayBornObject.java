package com.liberty.book.crawler.common;

import com.liberty.book.crawler.entity.AuthorBorndateEntity;

import java.util.Calendar;

/**
 * Created by user on 04.06.2017.
 */
public class DayBornObject {
    public static void main(String[] args) {
        DayBornObject object = new DayBornObject();
        object.setYear(1960);
        object.setMonth(1);
        object.setDay(1);
        System.out.println(object.mapToEntity().getBornDate());
    }
    private Integer day;
    private Integer month;
    private Integer year;
    private String name;
    private String link;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public AuthorBorndateEntity mapToEntity(){
        AuthorBorndateEntity entity = new AuthorBorndateEntity();
        Calendar bornDate = Calendar.getInstance();
        bornDate.set(this.getYear(),this.getMonth()-1,this.getDay());
        entity.setBornDate(bornDate.getTimeInMillis()/1000);
        entity.setAuthorName(this.getName());
        entity.setLivelibAuthorId(this.getId());
        return entity;
    }
    @Override
    public String toString() {
        return "DayBornObject{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", id=" + id +
                '}';
    }
}
