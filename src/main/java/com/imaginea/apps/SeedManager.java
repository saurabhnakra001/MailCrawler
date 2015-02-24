package com.imaginea.apps;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author vamsi emani
 * 
 * Controls the processing of seeds with a queue.
 */
public class SeedManager {
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	LinkedBlockingQueue<MailSeed> queue = new LinkedBlockingQueue<MailSeed>();
	
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
			String fileName = "msg-"+i;
			String url = queue.poll().getDownloadUrl();
			System.out.println("Downloading from "+url+ " to "+fileName);
			Utility.download(url, fileName);			
			i++;
		}
	}
	
	public void printStatus(){
		System.out.println("seeds : "+ queue);
	}
}
