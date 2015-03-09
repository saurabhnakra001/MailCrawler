package com.imaginea.apps.crawler;

/**
 * @author vamsi emani
 * 
 * Externalize string constants. 
 */
public class StringConstants {

	public static final String BASEURL = "http://mail-archives.apache.org/mod_mbox/maven-users/";
	
	public static final String CANNOT_CRAWL = "Unable to run the crawler.";
	
	public static final String NUM_DOWNLOAD_WORKERS = "Enter number of download worker threads :";
	
	public static final String INVALID_NUM_WORKERS = "Invalid input, default download worker thread count set to 1";
	
	public static final String INVALID_LINK_GENERATE_WORKERS = "Invalid input, default link generate worker thread count set to 1";
	
	public static final String DEFAULT_INPUT_YEAR = "2014";

	public static final String NUM_LINK_GENERATE_WORKERS = "Enter number of link generate worker threads : ";

	public static final String CHECK_INTERNET_CONNECTION = "Please check your internet connection ...";
	
	public static final String CHECK_URL_OR_INTERNET_CONNECTION = "Please check entered url and internet connection ...";

	public static final Object ENTER_URL = "Enter url ("+BASEURL+") : ";

	public static final String ENTER_INPUT_YEAR = "Enter year : ";
	
	public static final String INVALID_INPUT_YEAR = "Entered year is invalid. Default set to "+DEFAULT_INPUT_YEAR;
	
	public static final String DECODE_ERROR = "Unable to decode ...";
	
	public static final String ENCODE_ERROR = "Unable to encode ...";
	
	public static final String CONFIG_FILE = "config.properties";
	
	public static final String QUEUE_DAT_FILE = "queue.dat";
	
	public static final String CANNOT_COLLECT_LINKS = "Cannot process links. Please check if page is valid ...";
	
	public static final String INTERRUPT_ERROR = "Interrupt error ...";		
	
	public static final String EXECUTION_ERROR = "Execution error ...";	
	
	public static final String DISK_LOAD_ERROR = "Error loading queue from disk ...";
	
}
