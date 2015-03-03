package com.imaginea.apps.crawler.processor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.imaginea.apps.crawler.Link;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

/**
 * 
 * @author vamsi emani
 *
 */
public interface SeedProcessor {

	public List<Future<WorkerRecord>> downloadSeeds(int number_of_workers);
	
	public List<Future<WorkerRecord>> generateSeeds(int number_of_workers, Queue<Link> links);
	
	public List<Callable<WorkerRecord>> getSeedConsumers(int number_of_workers);
	
	public List<Callable<WorkerRecord>> getSeedProducers(int number_of_workers, Queue<Link> links);
		
}
