package com.imaginea.apps.crawler.workers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.imaginea.apps.crawler.MailSeed;
import com.imaginea.apps.crawler.StringConstants;
import com.imaginea.apps.crawler.exceptions.CannotConnectException;
import com.imaginea.apps.crawler.exceptions.CrawlerException;
import com.imaginea.apps.crawler.workers.records.SeedConsumerRecord;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

/**
 * @author vamsi emani
 * Used to speed up the download process.
 * Seed consumer downloads the mails.
 */
public class SeedConsumer implements Callable<WorkerRecord>{

	private BlockingQueue<MailSeed> queue;	
	private WorkerRecord record;
	
	private static final Logger log = Logger.getLogger(SeedConsumer.class.getSimpleName());
	
	public SeedConsumer(BlockingQueue<MailSeed> queue) {
		this.queue = queue;
		this.record = new SeedConsumerRecord();
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

	public WorkerRecord call() throws CrawlerException, InterruptedException  {			
		SeedConsumerRecord rec = ((SeedConsumerRecord) record);
		rec.setOwner(Thread.currentThread());
		MailSeed seed = null;
		String url = "";
		try{
			while(!queue.isEmpty()){							
				seed = queue.take();				
				String fileName = "msg-"+seed.getUrlSuffix() + (queue.size()+1)+".txt";
				url = seed.getDownloadUrl();				
				String folder = "output"+File.separator+seed.getYear()+File.separator+seed.getMonth();
				log.info("\n>> DOWNLOADING FROM :\n\t"+url+ "\n\tto output/"+fileName);				
				download(folder, url, fileName);
				rec.downloaded();																	
			}
		}catch(IOException e){
			String msg = StringConstants.CHECK_INTERNET_CONNECTION+". Unable to download : "+url;
			seed.setDownloadFailed();
			((SeedConsumerRecord) record).failed();
			throw new CannotConnectException(msg);
		}	
		return record;
	}
}
