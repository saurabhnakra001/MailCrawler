package com.imaginea.apps;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * @author vamsi emani
 * 
 * This is used by the java crawler to invoke javascript code, 
 * without an actual browser,  so that dynamically generated 
 * mail links can be interpreted by the crawler for link extraction. 
 */
public class JSSandbox {

	/**
	 * Invokes the onload() function of archives.js to load all the messages.
	 */
	public HtmlPage loadMessages(HtmlPage page, String urlSuffix){
		String snippet = "loadBrowser ('/mod_mbox/maven-users/"+urlSuffix+"');";
		ScriptResult result = page.executeJavaScript(snippet);
		System.out.println("Is message list loaded : "+ result.getJavaScriptResult());
		return (HtmlPage) result.getNewPage();
	}
	
	/**
	 * the dynamically generated id of each mail message link is encoded 
	 * by default. This function invokes the std. js decode 
	 */
	public String decodeURIComponent(HtmlPage page, String text){
		String snippet = "decodeURIComponent('"+text+"');";
		ScriptResult result = page.executeJavaScript(snippet);
		String res = result.getJavaScriptResult().toString();
		return res;
	} 
}
