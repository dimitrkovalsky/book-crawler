package com.liberty.book.crawler.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.06.2017.
 */
public class BornDayParser {

    public BornDayParser(Calendar currentDay) {
        this.currentDay = currentDay;
    }

    private Calendar currentDay;

    public static void main(String[] args) {

    }

    public List<DayBornObject> process(String input){
        String filteredInput = filterInput(input);
        List<DayBornObject> outputList = new ArrayList<>();
        Pattern p = Pattern.compile(".*\"born-age-year\" title=\"(\\d+)\" style.*picture\" href=\"(.*)\" ><span c.*author/(\\d+)\">(.+)</a></div></div></div>");
        Matcher m = p.matcher(filteredInput );
        while(m.find()){
            DayBornObject dayBornObject = new DayBornObject();
            dayBornObject.setDay(currentDay.get(Calendar.DAY_OF_MONTH));
            dayBornObject.setMonth(currentDay.get(Calendar.MONTH));
            dayBornObject.setYear(currentDay.get(Calendar.YEAR)-Integer.parseInt(m.group(1)));
            dayBornObject.setLink(m.group(2));
            dayBornObject.setId(Integer.parseInt(m.group(3)));
            dayBornObject.setName(m.group(4));
            outputList.add(dayBornObject);
        }
        return outputList;
    }

    public String filterInput(String input){
        return input.replace("  ","").replace("\n","");
    }


}
