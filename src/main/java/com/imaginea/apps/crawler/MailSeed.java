package com.imaginea.apps.crawler;

import java.io.Serializable;

/**
 * @author vamsi emani
 * 
 * Mail seed object represents the mail link on the maven page 
 * that is to be extracted and downloaded.
 */

public class MailSeed implements Serializable{
	
	private String id, urlSuffix, baseUrl;

	private boolean downloadFailed;
	
	public MailSeed(String id, String urlSuffix){
		this.id = id;
		this.urlSuffix = urlSuffix;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}
	
	public String getId(){
		return id;
	}
	
	public String getDownloadUrl(){
		return getBaseUrl() + urlSuffix + "/raw/"+id;	
	}
	
	public String getUrlSuffix(){
		return urlSuffix;
	}		
	
	public String getMonth(){
		return urlSuffix.substring(4, 6);
	}
	
	public String getYear(){
		return urlSuffix.substring(0,4);
	}
	
	public void setDownloadFailed() {	
		downloadFailed = true;
	}
	
	public boolean isDownloadFailed(){
		return downloadFailed == true;
	}
	
	@Override
	public String toString() {	
		if(this.id != null && this.urlSuffix != null)
			return getDownloadUrl();
		return super.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		boolean isEqual= false;
	    if (object != null && object instanceof MailSeed){
	    	MailSeed obj = ((MailSeed) object);	    	
	        isEqual = this.id.equals(obj.id);
	    }
	    return isEqual;
	}
	
	@Override
	public int hashCode() {		
		return this.id.hashCode();
	}	
}
