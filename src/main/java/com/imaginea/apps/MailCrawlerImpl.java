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
		ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml");
		MailCrawler crawler = (MailCrawler) context.getBean("mailCrawlerBean");
		if(crawler.canResume())
			crawler.resume();
		else{
			MailCrawlerImpl impl = new MailCrawlerImpl();			
			crawler.setUrl(impl.getUrl());
			crawler.setInputYear(impl.getYear());
			int producerCount = impl.getLinkGenerateWorkerCount();
			int consumerCount = impl.getDownloadWorkerCount();							
			impl.runCrawler(crawler, producerCount, consumerCount);
		}
		
	}
		
	public void runCrawler(MailCrawler crawler, int producerCount, int consumerCount){		
		if(producerCount != -1)
			crawler.setLinkGenerateWorkerCount(producerCount);
		if(consumerCount != -1)
			crawler.setDownloadWorkerCount(consumerCount);		
		crawler.initializeWebClient();						
		if(crawler.canCrawl())
			crawler.crawl();
		else
			log.error(CANNOT_CRAWL);
	}
	
	public String readConsole(Object msg){
		Scanner scanner = new Scanner(System.in);
		System.out.println(msg);
		return scanner.nextLine();
	}
	
	public String getUrl(){
		return readConsole(ENTER_URL);
	}
	
	public String getYear(){
		return Integer.toString(getIntegerInput(ENTER_INPUT_YEAR, INVALID_INPUT_YEAR));
	}
	
	public int getDownloadWorkerCount(){
		return getIntegerInput(NUM_DOWNLOAD_WORKERS, INVALID_NUM_WORKERS);
	}
	
	public int getLinkGenerateWorkerCount(){
		return getIntegerInput(NUM_LINK_GENERATE_WORKERS, INVALID_LINK_GENERATE_WORKERS);
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
