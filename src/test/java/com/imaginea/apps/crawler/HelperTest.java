package com.imaginea.apps.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.imaginea.apps.crawler.AbstractMailCrawler;
import com.imaginea.apps.crawler.MailCrawler;

public class HelperTest {

	private AbstractMailCrawler crawler; 
	
	private String url;
	
	@Before
	public void setUp() throws Exception {
		crawler = new MailCrawler();
		crawler.setInputYear("2014");
		this.url = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/<20141201165933.302fe3b8@linux-du93.site>";
	}	

	@Test
	public void testDecodeUrl() {
		String url = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/%3C20141201165933.302fe3b8@linux-du93.site%3E";		
		String expected = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/<20141201165933.302fe3b8@linux-du93.site>";
		assertEquals(expected, crawler.getHelper().decodeUrl(url));
	}

	@Test
	public void testEncodeUrl() {				
		String expected = "http%3A%2F%2Fmail-archives.apache.org%2Fmod_mbox%2Fmaven-users%2F201412.mbox%2F%3C20141201165933.302fe3b8%40linux-du93.site%3E";
		assertEquals(expected, crawler.getHelper().encodeUrl(this.url));
	}

	@Test
	public void testUrlSuffixOfUrl() {
		assertEquals("201412.mbox", crawler.getHelper().urlSuffixOfUrl(this.url));
	}	

}
