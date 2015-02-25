package com.imaginea.apps;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author vamsi emani
 * 
 * Controls the processing of seeds with a queue.
 */
public class SeedProcessor {
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	LinkedBlockingQueue<MailSeed> queue = new LinkedBlockingQueue<MailSeed>();
	
	private static final Logger log = Logger.getLogger(SeedProcessor.class.getSimpleName());
	
	public void addNewSeed(String extracted, String suffix){
		String mailseed = extracted;
		if(extracted.startsWith("ajax/")){
			mailseed = mailseed.substring(mailseed.indexOf("/")+1);
			queue.add(new MailSeed(mailseed, suffix));
		}			
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 */
	public void downloadSeeds(){
		int i = 1;
		while(!queue.isEmpty()){
			String fileName = "msg-"+queue.poll().getUrlSuffix() + i+".txt";
			String url = queue.poll().getDownloadUrl();
			log.info("Downloading from :\n\t"+url+ " to Output/"+fileName);
			Utility.download(url, fileName);			
			i++;
		}
	}
	
	public void printStatus(){
		System.out.println("seeds : "+ queue);
	}
}
