package com.imaginea.apps;

import static com.imaginea.apps.crawler.StringConstants.ERRORS.cannot_crawl;
import static com.imaginea.apps.crawler.StringConstants.INPUTS.default_input_year;
import static com.imaginea.apps.crawler.StringConstants.INPUTS.enter_input_year;
import static com.imaginea.apps.crawler.StringConstants.INPUTS.enter_url;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.invalid_year;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.invalid_linkgen_worker_count;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.invalid_download_worker_count;
import static com.imaginea.apps.crawler.StringConstants.INPUTS.num_download_workers;
import static com.imaginea.apps.crawler.StringConstants.INPUTS.num_linkgen_workers;
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
			impl.runCrawler(crawler, impl.getLinkGenerateWorkerCount(), impl.getDownloadWorkerCount());
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
			log.error(cannot_crawl);
	}
	
	public String readConsole(Object msg){
		Scanner scanner = new Scanner(System.in, "UTF-8");
		System.out.println(msg);
		return scanner.nextLine();
	}
	
	public String getUrl(){
		return readConsole(enter_url);
	}
	
	public String getYear(){
		return Integer.toString(getIntegerInput(enter_input_year, invalid_year));
	}
	
	public int getDownloadWorkerCount(){
		return getIntegerInput(num_download_workers, invalid_download_worker_count);
	}
	
	public int getLinkGenerateWorkerCount(){
		return getIntegerInput(num_linkgen_workers, invalid_linkgen_worker_count);
	}
	
	/**
	 * Reads input from command line.
	 */
	public int getIntegerInput(String msg, String exceptionMsg){
		int i = -1;
		try{
			return Integer.parseInt(readConsole(msg));						
		}catch(NumberFormatException e){
			i = Integer.parseInt(default_input_year);
			log.info(exceptionMsg);					
		}
		return i;
	}
}
