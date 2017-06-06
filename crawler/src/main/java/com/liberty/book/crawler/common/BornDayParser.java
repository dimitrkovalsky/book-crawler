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
        String text = "<div class=\"born-author\"><span class=\"born-age-year\" title=\"110\" style=\"background-color: #FFD700;color:#39424c;\">110</span><a class=\"born-author-picture\" href=\"/author/5738\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/005738/45x45/c01a/Robert_Hajnlajn.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/5738\">Роберт Хайнлайн</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"42\" style=\"\">42</span><a class=\"born-author-picture\" href=\"/author/150374\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/150374/45x45/fafa/Zahar_Prilepin.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/150374\">Захар Прилепин</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"133\" style=\"\">133</span><a class=\"born-author-picture\" href=\"/author/10367\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/010367/45x45/5d05/Lion_Fejhtvanger.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/10367\">Лион Фейхтвангер</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"46\" style=\"\">46</span><a class=\"born-author-picture\" href=\"/author/234464\" ><span class=\"boocover\" style=\"background-image:url(https://j.livelib.ru/auface/234464/45x45/6c97/Melissa_de_la_Kruz.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/234464\">Мелисса де ла Круз</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"48\" style=\"\">48</span><a class=\"born-author-picture\" href=\"/author/193837\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/193837/45x45/74ec/Andrej_Astvatsaturov.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/193837\">Андрей Аствацатуров</a></div></div></div><div class=\"born-author\"><a class=\"born-author-picture\" href=\"/author/158966\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/158966/45x45/3ec2/Akino_Matsuri.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/158966\">Акино Мацури</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"30\" style=\"background-color: #FFD700;color:#39424c;\">30</span><a class=\"born-author-picture\" href=\"/author/384770\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/384770/45x45/58fb/Viktoriya_Shvab.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/384770\">Виктория Шваб</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"81\" style=\"\">81</span><a class=\"born-author-picture\" href=\"/author/167790\" ><span class=\"boocover\" style=\"background-image:url(https://i.livelib.ru/auface/167790/45x45/f711/Igor_Guberman.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/167790\">Игорь Губерман</a></div></div></div><div class=\"born-author\"><span class=\"born-age-year\" title=\"49\" style=\"\">49</span><a class=\"born-author-picture\" href=\"/author/157197\" ><span class=\"boocover\" style=\"background-image:url(https://j.livelib.ru/auface/157197/45x45/ad5a/Dzheff_Vandermeer.jpg)\"></span></a><div class=\"born-author-title\"><div class=\"born-author-td\"><a href=\"/author/157197\">Джефф Вандермеер</a></div></div></div>";
        BornDayParser bdp = new BornDayParser(Calendar.getInstance());
        bdp.process(text);
    }

    public List<DayBornObject> process(String input){
        String filteredInput = filterInput(input);
        List<DayBornObject> outputList = new ArrayList<>();
        System.out.println(filteredInput);
        Pattern p = Pattern.compile("title=\"(\\d{1,4}).{1,350}picture\" href=\"(.{1,350})\" ><span c.{1,350}author/(\\d{1,350})\">(.{1,340})</a></div></div></div>");
        Matcher m = p.matcher(filteredInput );
        while(m.find()){
            DayBornObject dayBornObject = new DayBornObject();
            dayBornObject.setDay(currentDay.get(Calendar.DAY_OF_MONTH));
            dayBornObject.setMonth(currentDay.get(Calendar.MONTH));
            dayBornObject.setYear(currentDay.get(Calendar.YEAR)-Integer.parseInt(m.group(1)));
            dayBornObject.setLink(m.group(2));
            dayBornObject.setId(Long.parseLong(m.group(3)));
            dayBornObject.setName(m.group(4));
            outputList.add(dayBornObject);
            System.out.println(dayBornObject);
        }
        return outputList;
    }

    public String filterInput(String input){
        return input.replace("  ","").replace("\n","");
    }


}
