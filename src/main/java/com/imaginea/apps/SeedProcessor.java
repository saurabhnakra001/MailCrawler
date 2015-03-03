package com.imaginea.apps;

import java.util.List;

/**
 * 
 * @author vamsi emani
 *
 */
public interface SeedProcessor {

	public void downloadSeeds(int number_of_workers);
	
	public void generateSeeds(int number_of_workers, List<Link> links);
}
