package com.liberty.book.crawler.common;

/**
 * Created by user on 04.06.2017.
 */
public class DayBornObject {
    private Integer day;
    private Integer month;
    private Integer year;
    private String name;
    private String link;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
