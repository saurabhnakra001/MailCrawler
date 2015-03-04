package com.imaginea.apps.crawler.processor;

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

import com.imaginea.apps.crawler.Link;
import com.imaginea.apps.crawler.MailCrawler;
import com.imaginea.apps.crawler.MailSeed;
import com.imaginea.apps.crawler.workers.SeedConsumer;
import com.imaginea.apps.crawler.workers.SeedProducer;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

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
	
	public List<Callable<WorkerRecord>> getSeedConsumers(int number_of_workers){
		List<Callable<WorkerRecord>> tasks = new ArrayList<Callable<WorkerRecord>>();
		for( int i = 0 ; i < number_of_workers ; i++)				
			tasks.add(new SeedConsumer(queue));
		return tasks;
	}
	
	public List<Callable<WorkerRecord>> getSeedProducers(int number_of_workers, Queue<Link> links){
		List<Callable<WorkerRecord>> tasks = new ArrayList<Callable<WorkerRecord>>();
		ConcurrentHashMap<String, MailSeed> map = new ConcurrentHashMap<String, MailSeed>();
		for( int i = 0 ; i < number_of_workers ; i++){
			SeedProducer worker = new SeedProducer(queue, links);
			worker.setWebClient(crawler.getWebClient());
			worker.setVisited(map);
			tasks.add(worker);
		}
		return tasks;
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 */
	public List<Future<WorkerRecord>> downloadSeeds(int number_of_workers){
		ExecutorService executorService = Executors.newFixedThreadPool(number_of_workers);			
		List<Future<WorkerRecord>> futures = null;		
		try {
			futures = executorService.invokeAll(getSeedConsumers(number_of_workers));
			printStatistics(futures);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}		

		executorService.shutdown();	
		return futures;
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 * @return 
	 */
	public List<Future<WorkerRecord>> generateSeeds(int number_of_workers, Queue<Link> links){
		ExecutorService executorService = Executors.newFixedThreadPool(number_of_workers);			
		List<Future<WorkerRecord>> futures = null;		
		try {
			futures = executorService.invokeAll(getSeedProducers(number_of_workers, links));
			printStatistics(futures);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}		

		executorService.shutdown();	
		return futures;
	}
	
	
	public void printStatistics(List<Future<WorkerRecord>> futures) throws InterruptedException, ExecutionException{
		for(Future<WorkerRecord> future : futures){
		    log.info(future.get().status());
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
