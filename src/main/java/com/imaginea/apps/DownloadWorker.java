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
public class DownloadWorker implements Callable<DownloadRecord>{

	BlockingQueue<MailSeed> queue;	
	DownloadRecord record = new DownloadRecord();
	
	private static final Logger log = Logger.getLogger(DownloadWorker.class.getSimpleName());
	
	public DownloadWorker(BlockingQueue<MailSeed> queue) {
		this.queue = queue;
	}
		
	private synchronized void download(String folder, String urlStr, String fileName) throws IOException{
		URL url;		
		new File(folder).mkdirs();
		url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());							
		FileOutputStream fos = new FileOutputStream(new File(folder + File.separator + fileName));
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

	public DownloadRecord call() throws Exception {	
		record.setOwner(Thread.currentThread());
		while(!queue.isEmpty()){			
			try {
				MailSeed seed = queue.take();				
				String fileName = "msg-"+seed.getUrlSuffix() + (queue.size()+1)+".txt";
				String url = seed.getDownloadUrl();
				String folder = "output"+File.separator+seed.getYear()+File.separator+seed.getMonth();
				log.info("\n>> DOWNLOADING FROM :\n\t"+url+ " to output/"+fileName);
				try {
					download(folder, url, fileName);
					record.downloaded();
				} catch (Exception e) {
					log.severe("Unable to download : "+url);
					seed.setDownloadFailed();
					record.failed();
				}							
			} catch (InterruptedException e1) {				
				e1.printStackTrace();
			}			
		}
		//String stats = Thread.currentThread().getName()+record.status();
		return record;
	}
}
