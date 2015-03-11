package com.imaginea.apps.crawler;

/**
 * @author vamsi emani
 * 
 * Externalize string constants. 
 */
public final class StringConstants {
	
	public static final class FILENAMES{				
		public static final String config_file = "config.properties",				
		queue_dat_file = "queue.dat";
	}
	
	public static final class ERRORS{		
		public static final String check_internet_connection = "Please check your internet connection ...",				
		check_url_internet_connection = "Please check entered url and internet connection ...",		
		decode_error = "Unable to decode ...",		
		encode_error = "Unable to encode ...",	
		invalid_year = "Entered year is invalid. Default set to "+INPUTS.default_input_year,		
		invalid_download_worker_count = "Invalid input, default download worker thread count set to 1 ...",	
		invalid_linkgen_worker_count = "Invalid input, default link generate worker thread count set to 1",		
		cannot_crawl = "Unable to run the crawler ...",
		cannot_collect_links = "Cannot process links. Please check if page is valid ...",		
		interrupt_error = "Interrupt error ...",				
		execution_error = "Execution error ...",			
		disk_load_error = "Error loading queue from disk ...",
		dat_file_delete_error = "Error deleting dat file ...",
		cannot_write_error = "Cannot write to download folder ...";
	}
	
	public static final class INPUTS{		
		public static final String base_url = "http://mail-archives.apache.org/mod_mbox/maven-users/",				
		num_download_workers = "Enter number of download worker threads :",		
		default_input_year = "2014",
		num_linkgen_workers = "Enter number of link generate worker threads : ",
		enter_url = "Enter url ("+base_url+") : ",
		enter_input_year = "Enter year : ";
	}
}
