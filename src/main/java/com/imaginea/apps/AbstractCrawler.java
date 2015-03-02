package com.imaginea.apps;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author vamsi emani
 *
 * Subclass this and implement abstract methods
 * to provide your own implementation of the crawler.
 */

public abstract class AbstractCrawler implements WebSpider {
	
	SeedProcessor processor = new SeedProcessor();	

	Scanner scanner = new Scanner(System.in);

	protected static int DOWNLOAD_WORKER_THREADS = 1;
	
	/**
	 * Reads input from command line.
	 */
	public void consumeInputs(){
		try{
			DOWNLOAD_WORKER_THREADS = Integer.parseInt(readInput(StringConstants.NUM_DOWNLOAD_WORKERS));
		}catch(NumberFormatException e){
			System.out.println(StringConstants.INVALID_NUM_WORKERS);
			DOWNLOAD_WORKER_THREADS = 1; 			
		}
	}

	public void run() {
		collectHyperlinks();
		processLinksOnWebPage();
		processor.addSeeds(extractSeeds());
		download();
	}

	public abstract void collectHyperlinks();
	
	public abstract void processLinksOnWebPage();
	
	public abstract List<MailSeed> extractSeeds();		
		
	public String readInput(Object msg){
		System.out.println(msg);
		return scanner.nextLine();
	}
	
	public boolean canCrawl() {
		return true;
	}

	public void download() {		
		processor.downloadSeeds(DOWNLOAD_WORKER_THREADS); 		
	}

}
