package com.imaginea.apps.crawler.processor;

import static com.imaginea.apps.crawler.StringConstants.FILENAMES.QUEUE_DAT_FILE;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.DAT_FILE_DELETE_ERROR;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.INTERRUPT_ERROR;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.EXECUTION_ERROR;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.DISK_LOAD_ERROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import org.apache.log4j.Logger;

import com.imaginea.apps.crawler.Link;
import com.imaginea.apps.crawler.MailCrawler;
import com.imaginea.apps.crawler.MailSeed;
import com.imaginea.apps.crawler.exceptions.ConnectException;
import com.imaginea.apps.crawler.workers.SeedConsumer;
import com.imaginea.apps.crawler.workers.SeedProducer;
import com.imaginea.apps.crawler.workers.records.SeedConsumerRecord;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

/**
 * @author vamsi emani
 * 
 * Controls the processing of seeds with a queue.
 */
public class MailSeedProcessor implements SeedProcessor{
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	private LinkedBlockingQueue<MailSeed> queue;	
	private MailCrawler crawler;			
	private boolean resumeState = false;
	private static final Logger log = Logger.getLogger(MailSeedProcessor.class);

	public MailSeedProcessor(MailCrawler crawler) {
		this.crawler = crawler;
		this.queue = reloadQueueFromDisk();
	}			
	
	public boolean hasSeeds(){
		return !queue.isEmpty();
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
			worker.setCrawler(crawler);
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
			log.error(INTERRUPT_ERROR, e);
		} catch (ExecutionException e) {			
			handleError(e);
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
			log.error(INTERRUPT_ERROR, e);
		} catch (ExecutionException e) {
			handleError(e);
		}
		executorService.shutdown();	
		return futures;
	}
	
	private void handleError(ExecutionException e){
		if(e.getCause() instanceof ConnectException)
			log.error(e.getMessage());					
		else
			log.error(EXECUTION_ERROR, e);
		saveQueueToDisk();
	}
		
	private void saveQueueToDisk(){
		try {
			FileOutputStream fout = new FileOutputStream(QUEUE_DAT_FILE);
		    ObjectOutputStream oos = new ObjectOutputStream(fout);
		    oos.writeObject(queue);
		    oos.close();
		}
		catch (Exception e) { 
			log.error(DISK_LOAD_ERROR, e);
		}
	}
	
	private LinkedBlockingQueue<MailSeed> reloadQueueFromDisk(){		
		try {
			FileInputStream fin = new FileInputStream(QUEUE_DAT_FILE);
		    ObjectInputStream ois = new ObjectInputStream(fin);
		    queue = (LinkedBlockingQueue<MailSeed>) ois.readObject();
		    if(hasSeeds())
		    	setResumeState(true);
		    ois.close();		    
		}
		catch (IOException e) {
			queue = new LinkedBlockingQueue<MailSeed>();			
		}catch (ClassNotFoundException e) {
			queue = new LinkedBlockingQueue<MailSeed>();			
		}
		finally{
			File datFile = new File(QUEUE_DAT_FILE);
			if(datFile.exists() && datFile.delete())
				log.error(DAT_FILE_DELETE_ERROR);
		}
		return queue;
	}
	
	public void printStatistics(List<Future<WorkerRecord>> futures) throws InterruptedException, ExecutionException{
		for(Future<WorkerRecord> future : futures){
		    log.info(future.get().status());
		}
	}
	
	public int getDownloadCount(List<Future<WorkerRecord>> futures){
		int count = 0 ;
		for(Future<WorkerRecord> future : futures){
		    try {
				count = count + ((SeedConsumerRecord) future.get()).getDownloadedCount();
			} catch (InterruptedException e) {				
				log.error(INTERRUPT_ERROR, e);
			}catch(ExecutionException e){
				log.error(EXECUTION_ERROR, e);
			}
		}
		return count;
	}
			
	public boolean isResumeState() {
		return resumeState;
	}

	public void setResumeState(boolean resumeState) {
		this.resumeState = resumeState;
	}
}
