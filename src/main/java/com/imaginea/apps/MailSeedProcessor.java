package com.imaginea.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author vamsi emani
 * 
 * Controls the processing of seeds with a queue.
 */
public class MailSeedProcessor implements SeedProcessor{
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	private LinkedBlockingQueue<MailSeed> queue = new LinkedBlockingQueue<MailSeed>();	

	private MailCrawler crawler;
	
	private static final Logger log = Logger.getLogger(MailSeedProcessor.class.getSimpleName());
	
	public MailSeedProcessor(MailCrawler crawler) {
		this.crawler = crawler;
	}
		
	public void addSeed(MailSeed seed){
		queue.offer(seed);		
	}
	
	public void addSeeds(List<MailSeed> seeds){
		for(MailSeed seed : seeds)
			addSeed(seed);
	}	
	
	private List<Callable<DownloadRecord>> getConsumerWorkers(int number_of_workers){
		List<Callable<DownloadRecord>> tasks = new ArrayList<Callable<DownloadRecord>>();
		for( int i = 0 ; i < number_of_workers ; i++)				
			tasks.add(new DownloadWorker(queue));
		return tasks;
	}
	
	private List<Callable<String>> getProducerWorkers(int number_of_workers, Queue<Link> links){
		List<Callable<String>> tasks = new ArrayList<Callable<String>>();
		ConcurrentHashMap<String, MailSeed> map = new ConcurrentHashMap<String, MailSeed>();
		for( int i = 0 ; i < number_of_workers ; i++){
			LinkWorker worker = new LinkWorker(queue, links);
			worker.setWebClient(crawler.getWebClient());
			worker.setVisited(map);
			tasks.add(worker);
		}
		return tasks;
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 */
	public void downloadSeeds(int number_of_workers){
		ExecutorService executorService = Executors.newFixedThreadPool(number_of_workers);			
		List<Future<DownloadRecord>> futures = null;		
		try {
			futures = executorService.invokeAll(getConsumerWorkers(number_of_workers));
			printStatistics(futures);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}		

		executorService.shutdown();			
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 */
	public void generateSeeds(int number_of_workers, Queue<Link> links){
		ExecutorService executorService = Executors.newFixedThreadPool(number_of_workers);			
		List<Future<String>> futures = null;		
		try {
			futures = executorService.invokeAll(getProducerWorkers(number_of_workers, links));
			//printStatistics(futures);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}		

		executorService.shutdown();			
	}
	
	
	public void printStatistics(List<Future<DownloadRecord>> futures) throws InterruptedException, ExecutionException{
		for(Future<DownloadRecord> future : futures){
		    System.out.println(future.get().status());
		}
	}
	
	public List<MailSeed> getFailedSeeds(){
		List failed = new ArrayList();
		MailSeed[] seeds = (MailSeed[]) queue.toArray();
		for (int i = 0; i < seeds.length; i++) {
			if(seeds[i].isDownloadFailed())
				failed.add(seeds[i]);
		}
		return failed;
	}
			
}
