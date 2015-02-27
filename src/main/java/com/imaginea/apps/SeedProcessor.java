package com.imaginea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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
public class SeedProcessor {
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	LinkedBlockingQueue<MailSeed> queue = new LinkedBlockingQueue<MailSeed>();
	
	private static final Logger log = Logger.getLogger(SeedProcessor.class.getSimpleName());
	
	public void addSeed(MailSeed seed){
		queue.offer(seed);		
	}
	
	public void addSeeds(List<MailSeed> seeds){
		for(MailSeed seed : seeds)
			addSeed(seed);
	}
	
	private List<Callable<DownloadRecord>> getWorkers(int number_of_workers){
		List<Callable<DownloadRecord>> tasks = new ArrayList<Callable<DownloadRecord>>();
		for( int i = 0 ; i < number_of_workers ; i++)				
			tasks.add(new DownloadWorker(queue));
		return tasks;
	}
	/**
	 * Downloads the seeds in queue.. 
	 */
	public void downloadSeeds(int number_of_workers){
		ExecutorService executorService = Executors.newFixedThreadPool(number_of_workers);			
		List<Future<DownloadRecord>> futures = null;		
		try {
			futures = executorService.invokeAll(getWorkers(number_of_workers));
			printStatistics(futures);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
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
