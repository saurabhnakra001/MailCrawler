package com.imaginea.apps;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * 
 * @author vamsi emani
 *
 */
public class MailCrawlerImpl {

	private static Logger log = Logger.getLogger(MailCrawlerImpl.class.getSimpleName());
	
	
	public static void main(String[] args) {		
		MailCrawlerImpl impl = new MailCrawlerImpl();
		int cLinkGen = impl.getIntegerInput(StringConstants.NUM_LINK_GENERATE_WORKERS, StringConstants.INVALID_LINK_GENERATE_WORKERS);
		int cDownload = impl.getIntegerInput(StringConstants.NUM_DOWNLOAD_WORKERS, StringConstants.INVALID_NUM_WORKERS);	
		Crawler crawler = impl.initCrawler(cLinkGen, cDownload);		
		crawler.initializeWebClient();		
		impl.runCrawler(crawler);
		crawler.closeWebClient();
	}

	public void runCrawler(Crawler crawler){
		if(crawler.canCrawl())
			crawler.crawl();
		else
			log.severe(StringConstants.CANNOT_CRAWL);
	}
	
	public Crawler initCrawler(int num_link_gen_worker, int num_dwn_worker){
		Crawler crawler = new Crawler();
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
			log.info(exceptionMsg);					
		}
		return i;
	}
}
