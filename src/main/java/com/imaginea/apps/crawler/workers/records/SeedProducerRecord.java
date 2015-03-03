package com.imaginea.apps.crawler.workers.records;

/**
 * @author vamsi emani
 */

public class SeedProducerRecord extends WorkerRecord {

	private int seeded = 0;
	
	public void seed(){
		seeded++;
	}
	
	@Override
	public String status() {		
		return super.status()+" successfully seeded "+seeded+" mails.";
	}
}
