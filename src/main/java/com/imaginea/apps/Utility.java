package com.imaginea.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * @author vamsi emani
 * Utility and helper methods here. 
 */
public class Utility {

	/** If ever needed (?) **/
	public static void printPage(Page p) throws IOException{
		InputStream is = p.getWebResponse().getContentAsStream();
		int b = 0;
		while ((b = is.read()) != -1){
		    System.out.print((char)b);
		}
	}			
	
	/**
	 * Halt execution for specified time in ms,
	 */
	public static void sleep(int num){
		try {
			Thread.currentThread().sleep(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
