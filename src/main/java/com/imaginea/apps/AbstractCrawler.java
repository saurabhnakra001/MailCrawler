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

	String urlSuffix;	
	
	protected static int DOWNLOAD_WORKER_THREADS = 1;
	
	/**
	 * Reads input from command line.
	 */
	public void consumeInputs(){
		String year = readInput("Enter year in YYYY format (example - 2014) : ");		
		String month = readInput("Enter month in MM format (example - 02) : ");		
		this.urlSuffix = year + month + ".mbox";				
	}

	public void run() {
		processWebPage();
		processor.addSeeds(extractLinks());
		download();
	}

	public abstract void processWebPage();
	
	public abstract List<MailSeed> extractLinks();		
		
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
