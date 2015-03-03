package com.imaginea.apps.crawler;

/**
 * @author vamsi emani
 * 
 * Mail seed object represents the mail link on the maven page 
 * that is to be extracted and downloaded.
 */

public class MailSeed {
	
	private String id, urlSuffix;
	
	private boolean downloadFailed;
	
	public MailSeed(String id, String urlSuffix){
		this.id = id;
		this.urlSuffix = urlSuffix;
	}
	
	public String getId(){
		return id;
	}
	
	public String getDownloadUrl(){
		return StringConstants.BASEURL + urlSuffix + "/raw/"+id;	
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
	        isEqual = this.id == obj.id;
	    }
	    return isEqual;
	}
	
	@Override
	public int hashCode() {		
		return super.hashCode();
	}	
}
