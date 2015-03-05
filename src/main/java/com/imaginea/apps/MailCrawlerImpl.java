package com.imaginea.apps;

import static com.imaginea.apps.crawler.StringConstants.CANNOT_CRAWL;
import static com.imaginea.apps.crawler.StringConstants.DEFAULT_INPUT_YEAR;
import static com.imaginea.apps.crawler.StringConstants.ENTER_INPUT_YEAR;
import static com.imaginea.apps.crawler.StringConstants.ENTER_URL;
import static com.imaginea.apps.crawler.StringConstants.INVALID_INPUT_YEAR;
import static com.imaginea.apps.crawler.StringConstants.INVALID_LINK_GENERATE_WORKERS;
import static com.imaginea.apps.crawler.StringConstants.INVALID_NUM_WORKERS;
import static com.imaginea.apps.crawler.StringConstants.NUM_DOWNLOAD_WORKERS;
import static com.imaginea.apps.crawler.StringConstants.NUM_LINK_GENERATE_WORKERS;

import java.util.Scanner;





import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imaginea.apps.crawler.MailCrawler;

/**
 * 
 * @author vamsi emani
 *
 */
public class MailCrawlerImpl {

	private static Logger log = Logger.getLogger(MailCrawlerImpl.class.getSimpleName());
	
	public static void main(String[] args) {		
		MailCrawlerImpl impl = new MailCrawlerImpl();
		String url = impl.readConsole(ENTER_URL);
		int year = impl.getIntegerInput(ENTER_INPUT_YEAR, INVALID_INPUT_YEAR);
		int cLinkGen = impl.getIntegerInput(NUM_LINK_GENERATE_WORKERS, INVALID_LINK_GENERATE_WORKERS);
		int cDownload = impl.getIntegerInput(NUM_DOWNLOAD_WORKERS, INVALID_NUM_WORKERS);		
		MailCrawler crawler = impl.initCrawler(url, year, cLinkGen, cDownload);		
		crawler.initializeWebClient();		
		impl.runCrawler(crawler);		
	}

	public void runCrawler(MailCrawler crawler){
		if(crawler.canCrawl())
			crawler.crawl();
		else
			log.error(CANNOT_CRAWL);
	}
	
	public MailCrawler initCrawler(String url, int yr, int num_link_gen_worker, int num_dwn_worker){
		ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml");
		MailCrawler crawler = (MailCrawler) context.getBean("mailCrawlerBean");
		crawler.setUrl(url);
		crawler.setInputYear(Integer.toString(yr));
		if(num_link_gen_worker != -1)
			crawler.setLinkGenerateWorkerCount(num_link_gen_worker);
		if(num_dwn_worker != -1)
			crawler.setDownloadWorkerCount(num_dwn_worker);
		return crawler;
	}
	
	public String readConsole(Object msg){
		Scanner scanner = new Scanner(System.in);
		System.out.println(msg);
		return scanner.nextLine();
	}
	
	/**
	 * Reads input from command line.
	 */
	public int getIntegerInput(String msg, String exceptionMsg){
		int i = -1;
		try{
			return Integer.parseInt(readConsole(msg));						
		}catch(NumberFormatException e){
			i = Integer.parseInt(DEFAULT_INPUT_YEAR);
			log.info(exceptionMsg);					
		}
		return i;
	}
}
