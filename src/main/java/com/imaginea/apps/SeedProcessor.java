package com.imaginea.apps;

import java.util.List;
import java.util.Queue;

/**
 * 
 * @author vamsi emani
 *
 */
public interface SeedProcessor {

	public void downloadSeeds(int number_of_workers);
	
	public void generateSeeds(int number_of_workers, Queue<Link> links);
}
