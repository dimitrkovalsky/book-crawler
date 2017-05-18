package com.liberty.book.crawler.main;

import com.liberty.book.crawler.crawler4j.BookCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
@Configuration

public class App {


	public static void main(String[] args) throws Exception{

		String crawlStorageFolder = "C:/Users/user/IdeaProjects/book-crawler/dump";
		int numberOfCrawlers = 100;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setIncludeHttpsPages(true);
		//config.setResumableCrawling(true);
		config.setIncludeBinaryContentInCrawling(true);
		config.setMaxConnectionsPerHost(1000);
		config.setMaxTotalConnections(10000);
		config.setConnectionTimeout(1000);
		config.setPolitenessDelay(1);
		config.setThreadShutdownDelaySeconds(1);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */

		controller.addSeed("http://flisland.net/Aa");
		controller.addSeed("http://flisland.net/Bb");
		controller.addSeed("http://flisland.net/V");
		controller.addSeed("http://flisland.net/Gg");
		controller.addSeed("http://flisland.net/D");
		controller.addSeed("http://flisland.net/E");
		controller.addSeed("http://flisland.net/Zh");
		controller.addSeed("http://flisland.net/Z");
		controller.addSeed("http://flisland.net/I");
		controller.addSeed("http://flisland.net/");

		//controller.addSeed("http://flibusta.is/O");


        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */




		controller.start(new BookCrawlerFactory(), numberOfCrawlers);


	}

}