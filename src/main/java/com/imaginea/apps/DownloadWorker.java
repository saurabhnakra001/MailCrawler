package com.imaginea.apps;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * @author vamsi emani
 * Used to speed up the download process.
 */
public class DownloadWorker implements Runnable{

	BlockingQueue<MailSeed> queue;
	
	private static final Logger log = Logger.getLogger(DownloadWorker.class.getSimpleName());
	
	public DownloadWorker(BlockingQueue<MailSeed> queue) {
		this.queue = queue;
	}
	
	public void run() {	
		int i = 1;
		while(!queue.isEmpty()){			
			try {
				MailSeed seed = queue.take();
				String fileName = "msg-"+seed.getUrlSuffix() + i+".txt";
				String url = seed.getDownloadUrl();
				log.info("\n>> DOWNLOADING FROM :\n\t"+url+ " to output/"+fileName);
				try {
					Utility.download(url, fileName);
				} catch (Exception e) {
					log.severe("Unable to download : "+url);
				}			
				i++;
			} catch (InterruptedException e1) {				
				e1.printStackTrace();
			}			
		}
	}

}
