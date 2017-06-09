package com.liberty.book.crawler.quotes;

import com.liberty.book.crawler.common.RequestHelper;
import com.liberty.book.crawler.entity.AuthorEntity;
import com.liberty.book.crawler.entity.QuoteAuthorEntity;
import com.liberty.book.crawler.entity.QuoteEntity;
import com.liberty.book.crawler.repository.AuthorRepository;
import com.liberty.book.crawler.repository.QuoteAuthorRepository;
import com.liberty.book.crawler.repository.QuoteRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author dkovalskyi
 * @since 01.06.2017
 */
@Component
public class QuotesCrawler {
    private static AtomicInteger tagCounter = new AtomicInteger(0);
    private static AtomicInteger authorCounter = new AtomicInteger(0);
    private static AtomicLong quoteCounter = new AtomicLong(0);
    private static volatile Map<String, Integer> tags = new HashMap<>();// todo: fix tags
    private static volatile Map<String, Integer> authors = new HashMap<>();
    private static volatile Set<QuoteAuthorEntity> allAuthors = new HashSet<>();
    private static volatile List<QuoteEntity> allQuotes = new ArrayList<>();

    @Autowired
    private QuoteAuthorRepository quoteAuthorRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    public void crawl() {
        String retrieved = RequestHelper.executeRequestAndGetResult("http://greatwords.ru/#authors");
        Document document = Jsoup.parse(retrieved);
        Elements tags = document.select("#authors-con .tags").select("a");
        List<AuthorLink> links = new ArrayList<>();
        tags.forEach(t -> {
            String name = t.text();
            String url = t.attr("href");
            links.add(new AuthorLink(name, url));
        });
        System.out.println("Fetched info about " + links.size() + " authors");
        fetchQuotes(links);
    }

    private void fetchQuotes(List<AuthorLink> links) {
        links.stream().forEach(l -> {
            System.out.println("Crawling quotes for : " + l.authorName);
            List<QuoteEntity> quotes;
            String url = "http://greatwords.ru" + l.url;
            String result = RequestHelper.executeRequestAndGetResult(url);
            Document document = Jsoup.parse(result);
            Elements quoteTags = document.select(".playlist-item");
            Elements info = document.select(".author-info");
            String bio = null;
            String authorName = l.authorName;
            Integer authorId = getAuthorId(authorName);
            if (info.size() != 0) {
                bio = info.text().trim();
            }
            List<Long> quoteIds = new ArrayList<>();
            quoteTags.forEach(q -> {
                long id = Long.parseLong(q.attr("id").replace("pquote-", ""));
                quoteIds.add(id);
            });
            System.out.println("Found : " + quoteIds.size() + " quotes for " + l.authorName);
            quotes = crawlQuotes(quoteIds, authorId, authorName);
            System.out.println("Crawled : " + quotes.size() + " quotes for " + l.authorName);
            QuoteAuthorEntity authorEntity = new QuoteAuthorEntity(authorId, authorName, bio, null);
            quoteAuthorRepository.save(authorEntity);
            quoteRepository.save(quotes);
            allAuthors.add(authorEntity);
            allQuotes.addAll(quotes);
            System.out.println("All quotes : " + allQuotes.size());
            System.out.println("All authors : " + allAuthors.size() + "/" + links.size());
            System.out.println("All tags : " + tags.size());
            System.out.println(quoteTags.size());
        });
    }

    private Integer getAuthorId(String authorName) {
        return authors.computeIfAbsent(authorName, k -> authorCounter.incrementAndGet());
    }

    private List<QuoteEntity> crawlQuotes(List<Long> ids, Integer authorId, String authorName) {
        return ids.stream().map(id -> {
            String result = RequestHelper.executeRequestAndGetResult("http://greatwords.ru/quote/" + id);
            Document document = Jsoup.parse(result);
            Elements select = document.select(".greatwords");
            String text = select.text().trim();
            List<String> stringTags = document.select(".keyword").stream().map(Element::text).collect(Collectors.toList());

            QuoteEntity quote = new QuoteEntity();
            quote.setId(quoteCounter.incrementAndGet());
            quote.setText(text);
            quote.setQuoteAuthorId(authorId);
            quote.setQuoteAuthorName(authorName);
            quote.setTags(toMap(stringTags));
            return quote;
        }).collect(Collectors.toList());
    }

    private Map<Integer, String> toMap(List<String> stringTags) {
        Map<Integer, String> map = new HashMap<>();
        stringTags.forEach(t -> {
            Integer tagId = tags.computeIfAbsent(t, k -> tagCounter.incrementAndGet());
            map.put(tagId, t);

        });
        return map;
    }

    public void mapToFlibustaAuthors() {
        List<QuoteAuthorEntity> all = quoteAuthorRepository.findAll();
        System.out.println("Found : " + all.size() + " authors");
        AtomicInteger found = new AtomicInteger(1);
        AtomicInteger notFound = new AtomicInteger(1);
        all.forEach(a -> {
            String[] split = a.getAuthorName().split(" ");
            List<AuthorEntity> result;
            if (split.length == 1) {
                String lastName = split[0].toLowerCase();
                result = authorRepository.getByLastName(lastName);
            } else if (split.length == 2) {
                String firstName = split[0].toLowerCase();
                String lastName = split[1].toLowerCase();
                result = authorRepository.getByLastAndFistName(lastName,firstName);
            } else {
                String firstName = split[0].toLowerCase();
                String lastName = split[split.length - 1].toLowerCase();
                result = authorRepository.getByLastAndFistName(lastName,firstName);
            }
            System.out.println("Found : " + result.size() + " results for : " + a.getAuthorName());
            if (CollectionUtils.isEmpty(result)) {
                System.err.println("Not found authors for : " + a.getAuthorName());
                notFound.incrementAndGet();
            } else {
                AuthorEntity authorEntity = result.get(0);
                a.setFlibustaId(authorEntity.getAuthorId());
                quoteAuthorRepository.save(a);
                System.out.println("Selected " + authorEntity.getLastName()
                        + " with id : " + authorEntity.getAuthorId() + " for : " + a.getAuthorName());
                found.incrementAndGet();
                updateQuotes(a, authorEntity);
            }
            System.out.println("Processed : " + (found.get() + notFound.get()) + " / " + all.size());
            System.out.println("Found : " + (found.get() * 100 / notFound.get()) + " percents");
        });
    }

    private void updateQuotes(QuoteAuthorEntity quoteAuthorEntity, AuthorEntity authorEntity) {
        List<QuoteEntity> quotes = quoteRepository.findAllByQuoteAuthorId(quoteAuthorEntity.getId());
        quotes = quotes.stream().map(a -> {
            a.setFlibustaAuthorId(authorEntity.getAuthorId());
            return a;
        }).collect(Collectors.toList());
        System.out.println("Found " + quotes.size() + " quotes for " + quoteAuthorEntity.getAuthorName());
        quoteRepository.save(quotes);
    }


    public static class AuthorLink {
        public AuthorLink(String authorName, String url) {
            this.authorName = authorName;
            this.url = url;
        }

        public String authorName;
        public String url;
        public Integer id;
    }

    public static void main(String[] args) {
        QuotesCrawler crawler = new QuotesCrawler();
        crawler.crawl();
    }
}
