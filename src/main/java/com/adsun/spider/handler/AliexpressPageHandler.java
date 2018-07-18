package com.adsun.spider.handler;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.adsun.spider.core.HtmlPage;
import com.adsun.spider.core.Spider;
import com.adsun.spider.interfa.PageHandler;


public class AliexpressPageHandler extends PageHandler{
	private static Log log = LogFactory.getLog(AliexpressPageHandler.class);
	private static String keywords = "";
	public void handler(HtmlPage page) {
		System.out.println(page.getContext().asXML());
	}
	
	public static void main(String[] args) {
		keywords = "screen" + "+" + "protector";
		Spider spider = new Spider().addPageHandler(new AliexpressPageHandler()).domain("https://www.aliexpress.com/wholesale?SearchText=screen+protector").threadSize(3);
		spider.execute();
	}
}
