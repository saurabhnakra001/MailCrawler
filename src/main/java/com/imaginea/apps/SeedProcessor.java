package com.imaginea.apps;

import java.io.IOException;
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
			MailSeed seed = queue.poll();
			String fileName = "msg-"+seed.getUrlSuffix() + i+".txt";
			String url = seed.getDownloadUrl();
			log.info("\n>> DOWNLOADING FROM :\n\t"+url+ " to output/"+fileName);
			try {
				Utility.download(url, fileName);
			} catch (Exception e) {
				log.severe("Unable to download : "+url);
			}			
			i++;
		}
	}
	
	public void printStatus(){
		System.out.println("seeds : "+ queue);
	}
}
