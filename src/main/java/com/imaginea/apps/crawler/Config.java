package com.imaginea.apps.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author vamsi emani
 * 
 * config.properties under resources
 */
public class Config {

	private static Logger log = Logger.getLogger(Config.class);
	
	private Properties props;
	
	public Config load(){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		this.props = new Properties();
		try {
			InputStream resourceStream = loader.getResourceAsStream(StringConstants.CONFIG_FILE);
		    props.load(resourceStream);
		} catch (IOException e) {			
			log.error("Unable to load config.properties file");
		}
		return this;
	}
	
	public int timeout(){
		int timeout = asInteger(props.get("timeout"));
		timeout = (timeout == -1) ? 20000 : timeout;
		return timeout;
	}
	
	public int waitForBackgroundJavaScript(){
		int i = asInteger(props.get("waitForBackgroundJavaScript"));
		i = (i == -1) ? 60000 : i;
		return i;
	}
		
	public boolean redirectedEnabled(){
		return asBoolean(props.get("redirectedEnabled"));
	}
	
	public boolean javascriptEnabled(){
		return asBoolean(props.get("javascriptEnabled"));
	}	
	
	public boolean useInsecureSSL(){
		return asBoolean(props.get("javascriptEnabled"));
	}	
	
	public boolean cssEnabled(){
		return asBoolean(props.get("cssEnabled"));
	}		
			
	public int asInteger(Object i){
		int result = -1;
		try{
			result = Integer.parseInt(i.toString());
		}catch(NumberFormatException e){
			log.error("Invalid number");
		}
		return result;
	}
	
	public boolean asBoolean(Object b){
		return b.equals("true");			
	}
	
	public Properties getProperties(){
		return props;
	}
}
