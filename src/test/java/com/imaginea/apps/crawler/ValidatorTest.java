package com.imaginea.apps.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.imaginea.apps.crawler.AbstractMailCrawler;
import com.imaginea.apps.crawler.MailCrawler;
import com.imaginea.apps.crawler.Validator;

public class ValidatorTest {

	private AbstractMailCrawler crawler ;	
	
	@Before
	public void setUp() throws Exception {
		crawler = new MailCrawler();
		crawler.setInputYear("2014");
	}	

	@Test
	public void testIsValidPageLink() {
		Validator v = crawler.getValidator();
		assertEquals(true, v.isValidPageLink("/mod_mbox/maven-users/201412.mbox/thread?1"));
		assertEquals(false, v.isValidPageLink("%3cCA+nPnMwY5Awd21rfcYtXH3n45BFAfYkOswcY22U3dA4Dyuch8w@mail.gmail.com%3e"));		
		assertEquals(false, v.isValidPageLink("/mod_mbox/maven-users/201412.mbox/author?1"));
		assertEquals(false, v.isValidPageLink("/mod_mbox/maven-users/201412.mbox/date?2"));
	}
	
	public void testIsValidMailLink() {
		Validator v = crawler.getValidator();
		assertEquals(true, v.isValidMailLink("%3cCA+nPnMwY5Awd21rfcYtXH3n45BFAfYkOswcY22U3dA4Dyuch8w@mail.gmail.com%3e"));
		assertEquals(false, v.isValidMailLink("/mod_mbox/maven-users/201412.mbox/thread?1"));
	}

}
