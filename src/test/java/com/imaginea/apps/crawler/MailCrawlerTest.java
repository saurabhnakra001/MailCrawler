package com.imaginea.apps.crawler;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author vamsi emani
 *
 */
public class MailCrawlerTest {

	MailCrawler crawler;
	
	@Before
	public void setUp() throws Exception {
		this.crawler = new MailCrawler();		
		this.crawler.setInputYear("2014");
		this.crawler.setUrl("http://mail-archives.apache.org/mod_mbox/maven-users/");
		this.crawler.setDownloadWorkerCount(1);
		this.crawler.setLinkGenerateWorkerCount(1);
		this.crawler.initializeWebClient();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCollectHyperlinks() {
		Queue<Link> pagelinks = this.crawler.collectHyperlinks();
		assertEquals(true, pagelinks.size() > 0);	
		for(Link link : pagelinks){
			assertEquals(true, crawler.getValidator().isValidPageLink(link.href()));
		}
	}

	@Test
	public void testCanCrawl() {		
		String invalidUrl = "http://12334sdadasjhdahdaskjdhhsd"; 
		crawler.setUrl(invalidUrl);
		assertEquals(false, crawler.canCrawl());
		crawler.setUrl("http://mail-archives.apache.org/mod_mbox/maven-users/");
		assertEquals(true, crawler.canCrawl());
	}

	@Test(timeout=600000)
	public void testCrawl() {
		this.crawler.crawl();
		assertEquals(true, new File("output").exists());
		File folder = new File("output/2014");
		assertEquals(true, folder.exists() && folder.list().length == 12);
	}
	
	@Test(timeout=100000)
	public void testCrawlTime(){
		this.crawler.setDownloadWorkerCount(10);
		this.crawler.setLinkGenerateWorkerCount(10);
		this.crawler.crawl();
	}

	@Test
	public void testInitializeWebClient() {
		Config conf = new Config().load();
		assertEquals(conf.timeout(), this.crawler.getWebClient().getOptions().getTimeout());
		assertEquals(conf.cssEnabled(), this.crawler.getWebClient().getOptions().isCssEnabled());
		assertEquals(conf.javascriptEnabled(), this.crawler.getWebClient().getOptions().isJavaScriptEnabled());
		assertEquals(conf.redirectedEnabled(), this.crawler.getWebClient().getOptions().isRedirectEnabled());		
	}

}
