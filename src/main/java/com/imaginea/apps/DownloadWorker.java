package com.imaginea.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * @author vamsi emani
 * Used to speed up the download process.
 */
public class DownloadWorker implements Callable<String>{

	BlockingQueue<MailSeed> queue;
	private int download = 0;
	private int failed = 0;
	
	private static final Logger log = Logger.getLogger(DownloadWorker.class.getSimpleName());
	
	public DownloadWorker(BlockingQueue<MailSeed> queue) {
		this.queue = queue;
	}
		
	private synchronized void download(String urlStr, String fileName) throws IOException{
		URL url;		
		new File("Output").mkdir();
		url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());							
		FileOutputStream fos = new FileOutputStream(new File("Output" + File.separator + fileName));
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

	public String call() throws Exception {		
		while(!queue.isEmpty()){			
			try {
				MailSeed seed = queue.take();				
				String fileName = "msg-"+seed.getUrlSuffix() + (queue.size()+1)+".txt";
				String url = seed.getDownloadUrl();
				log.info("\n>> DOWNLOADING FROM :\n\t"+url+ " to output/"+fileName);
				try {
					download(url, fileName);
					download++;
				} catch (Exception e) {
					log.severe("Unable to download : "+url);
					seed.setDownloadFailed();
					failed++;
				}							
			} catch (InterruptedException e1) {				
				e1.printStackTrace();
			}			
		}
		String stats = Thread.currentThread().getName()+" successfully downloaded "+download+"/"+(download+failed)+" downloads.";
		return stats;
	}
}
