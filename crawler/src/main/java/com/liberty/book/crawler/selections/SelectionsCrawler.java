package com.liberty.book.crawler.selections;

import com.liberty.book.crawler.common.RequestHelper;
import com.liberty.book.crawler.entity.*;
import com.liberty.book.crawler.repository.*;
import com.liberty.book.crawler.service.TagService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 10.06.2017.
 */
@Service
public class SelectionsCrawler {

    private String baseDomain = "https://www.livelib.ru/";
    private Set<String> linkList = new HashSet<>();

    @Autowired
    private TagService tagService;

    @Autowired
    private SimpleBookRepository neurolibBookRepository;

    @Autowired
    private SelectionBookRepository selectionBookRepository;

    @Autowired
    private SelectionRepository selectionRepository;

    @Autowired
    private LivelibBookRepository livelibBookRepository;

    @Autowired
    private LinksRepository links;

    public void crawl() {
        ArrayList<String> visitUrlList = new ArrayList<>();
        visitUrlList.add(baseDomain+"service/ff");
        visitUrlList.add(baseDomain+"service/vag");
        RequestHelper.setUrlList(visitUrlList);
        getAllSelectionsLinks();
        System.out.println("Fetched  " + linkList.size() + " selections url");

        for (String link : linkList)
            crawlSelections(link);
    }

    public static void main(String[] args) {
        SelectionsCrawler crawler = new SelectionsCrawler();
        String retrieved = RequestHelper.executeRequestAndGetResult("https://www.livelib.ru/giveaways/selection/793385-razdachi-knig-izdatelstvo-ipi");
        Document document = Jsoup.parse(retrieved);
        System.out.println(crawler.parseGiveawaysData(document));
    }

    private String getNextPageLink(Document document){
        String link = document.select(".i-pager-next").parents().attr("href");
        if ("".equals(link)){
            return null;
        }
        else return link;
    }

    private String getNextPageLinkFromElements(Elements elements){
        String link = elements.select(".i-pager-next").parents().attr("href");
        if ("".equals(link)
                ||link.contains("/comments/")){
            return null;
        }
        else return link;
    }

    private List<String> parseSelectionsLinkList(Document document){
        List<String> links = new ArrayList<>();
        Elements tags = document.select(".selection").select(".event-title a");
        tags.forEach(element -> links.add(element.attr("href")));
        System.out.println("Fetched links:"+links.size());
        return links;
    }

    private void getAllSelectionsLinks(){
        String retrieved = RequestHelper.executeRequestAndGetResult(baseDomain+"/selections/top/");
        Document document = Jsoup.parse(retrieved);
        String nextPageLink = getNextPageLink(document);
        while (nextPageLink!=null){
            retrieved = RequestHelper.executeRequestAndGetResult(baseDomain+nextPageLink);
            document = Jsoup.parse(retrieved);
            linkList.addAll(parseSelectionsLinkList(document));
            nextPageLink = getNextPageLink(document);
        }
        System.out.println("This is last page");
    }

    public void crawlSelections(String link){
        if(links.getFirstByLinkEquals(link)==null) {
            System.out.println("Process " + link + " selection");
            String retrieved = RequestHelper.executeRequestAndGetResult(baseDomain + link);
            Document document = Jsoup.parse(retrieved);
            if(link.contains("giveaways/")||link.contains("group/")){
                selectionRepository.save(parseGiveawaysData(document));
            }else{
                selectionRepository.save(parseSelectionData(document));
            }
            Elements container = document.select("div.column-670.subcontainer div.block div.book-container.biglist").parents().select(".pager-ll2015b");
            String nextPageLink = getNextPageLinkFromElements(container);
            parseSelectionBooks(document);
            while (nextPageLink != null) {
                System.out.println("Process " + nextPageLink + " page of selection");
                retrieved = RequestHelper.executeRequestAndGetResult(baseDomain + nextPageLink);
                document = Jsoup.parse(retrieved);
                container = document.select("div.column-670.subcontainer div.block div.book-container.biglist").parents().select(".pager-ll2015b");
                nextPageLink = getNextPageLinkFromElements(container);
                parseSelectionBooks(document);
            }
            LinkEntity entity = new LinkEntity();
            entity.setLink(link);
            links.save(entity);
            System.out.println("Selection crawled");
        }else{
            System.out.println("Link" + link + " already processed");
        }
    }

    private SelectionEntity parseGiveawaysData(Document document){
        SelectionEntity selection = new SelectionEntity();
        String selectionId = document.select("#selection-id").attr("value");
        selection.setSelectionId(Long.parseLong(selectionId));

        Elements dataContainer = document.select(".column-670.subcontainer");
        String title = dataContainer.select("div.block > h1").text();
        if("".equals(title))
            title = dataContainer.select("div.column-670 > h1").text();
        selection.setTitle(title);
        selection.setUserMade(dataContainer.select("span.reader a.action").attr("title"));
        selection.setCreateTime(dataContainer.select("div.group-actionbar a.post-date").text());
        selection.setDescription(dataContainer.select(".group-selection-data div.description > p:lt(1)").html());
        Elements eventActionBar = dataContainer.select("div.event-actionbar");
        String voteText = eventActionBar.select("div.hand > span").text();
        if(voteText!=null&&!"".equals(voteText))
            selection.setVotes(Integer.parseInt(voteText));
        String likeText = eventActionBar.select("span.count-in-fav").text();
        if(likeText!=null&&!"".equals(likeText))
            selection.setLikes(Integer.parseInt(likeText));

        return selection;
    }

    private SelectionEntity parseSelectionData(Document document){
        SelectionEntity selection = new SelectionEntity();
        String selectionId = document.select("#selection-id").attr("value");
        selection.setSelectionId(Long.parseLong(selectionId));

        Elements dataContainer = document.select(".column-670.subcontainer");
        String title = dataContainer.select("div.block > h1").text();
        if("".equals(title))
            title = dataContainer.select("div.column-670 > h1").text();
        selection.setTitle(title);
        selection.setUserMade(dataContainer.select(".event-user-login.wordbreak a").attr("title"));
        selection.setCreateTime(dataContainer.select(".event-user-date span").text());
        selection.setDescription(dataContainer.select(".selection .description").html());
        Elements eventActionBar = dataContainer.select(".separator.selection-row .block-border .event-actionbar");
        String voteText = eventActionBar.select("span.vote.action.action-text").text();
        if(voteText!=null&&!"".equals(voteText))
            selection.setVotes(Integer.parseInt(voteText));
        String likeText = eventActionBar.select("span.count-in-fav").text();
        if(likeText!=null&&!"".equals(likeText))
            selection.setLikes(Integer.parseInt(likeText));

        Elements tagsLinks = dataContainer.select(".event-tags a");
        List<String> tags = new ArrayList<>();

        tagsLinks.forEach(element -> {
            tags.add(element.text());
        });
        tagService.tagSelection(selection.getSelectionId(),tags);
        return selection;
    }

    private List<SelectionBooksEntity> parseSelectionBookData(Elements bookElements,Long selectionId){
        List<SelectionBooksEntity> selectionBooks = new ArrayList<>();
        bookElements.forEach(element -> {
            SelectionBooksEntity selectionBooksEntity = new SelectionBooksEntity();
            String bookId = element.attr("id").replace("my-selection-book-list-tr-","");
            selectionBooksEntity.setLivelibBookId(Long.parseLong(bookId));
            selectionBooksEntity.setSelectionId(selectionId);
            SimpleBookEntity bookEntity = neurolibBookRepository.findFirstByTitleAndAndDeletedFalse(element.select(".block-book-title").text());
            selectionBooksEntity.setDescription(element.select(".book-description").before("<p class=\"unnoticeable\"").text());
            String votes = element.select("span.vote.action.action-text").text();
            if(votes!=null&&!"".equals(votes))
                selectionBooksEntity.setVotes(Integer.parseInt(votes));
            if(bookEntity!=null)
                selectionBooksEntity.setNeurolibBookId(bookEntity.getBookId());
            selectionBooks.add(selectionBooksEntity);

        });
        return selectionBooks;

    }

    private List<LivelibBookEntity> parseBookData(Elements bookElements){
        List<LivelibBookEntity> selectionBooksData = new ArrayList<>();
        bookElements.forEach(element -> {
            LivelibBookEntity bookEntity = new LivelibBookEntity();
            String bookId = element.attr("id").replace("my-selection-book-list-tr-","");
            bookEntity.setBookId(Long.parseLong(bookId));
            String authorId = element.select("a.block-book-author").attr("href").replace("/author/","").split("-")[0];
            bookEntity.setTitle(element.select("a.block-book-title").text());
            if(authorId!=null&&!"".equals(authorId))
                bookEntity.setAuthorId(Long.parseLong(authorId));
            bookEntity.setAuthorNames(element.select("a.block-book-author").text());
            bookEntity.setCover(element.select("span.boocover img").attr("src"));
            String rating = element.select("span.rating-book span").first().text();
            bookEntity.setRating(Float.parseFloat(rating));

            Elements tagsLinks = element.select("p.taglist a");
            List<String> tags = new ArrayList<>();

            tagsLinks.forEach(tagElements -> {
                tags.add(tagElements.text());
            });
            tagService.tagBook(bookEntity.getBookId(),tags);

            selectionBooksData.add(bookEntity);
        });
        return selectionBooksData;
    }

    private void parseSelectionBooks(Document document){
        String selectionIdString = document.select("#selection-id").attr("value");
        if("".equals(selectionIdString)){
            System.out.println(document.html());
        }
        Long selectionId = Long.parseLong(selectionIdString);
        Elements dataContainer = document.select(".selebook-wrapper");

        try {
            selectionBookRepository.save(parseSelectionBookData(dataContainer, selectionId));
            livelibBookRepository.save(parseBookData(dataContainer));
        } catch(DataIntegrityViolationException e) {
            System.out.println("Object is already exist in database");
        }
    }

    public void mapToNeurolib(){
        List<SelectionBooksEntity> allSelectionBook = selectionBookRepository.findAll();

        for(SelectionBooksEntity selectionBooksEntity:allSelectionBook){
            Long livelibBookId = selectionBooksEntity.getLivelibBookId();
            System.out.println(selectionBooksEntity);
            LivelibBookEntity livelibBookEntity= livelibBookRepository.getOne(livelibBookId);
            if(livelibBookEntity!=null) {
                String title = livelibBookEntity.getTitle();
                SimpleBookEntity neurolibBook = neurolibBookRepository.findFirstByTitleAndAndDeletedFalse(title);
                if(neurolibBook!=null){
                    selectionBooksEntity.setNeurolibBookId(neurolibBook.getBookId());
                    selectionBookRepository.save(selectionBooksEntity);
                }
            }
        }

    }

}
