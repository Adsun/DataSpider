package com.adsun.spider.handler;


import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.dom4j.Node;

import com.adsun.spider.core.HtmlPage;
import com.adsun.spider.core.Spider;


public class JDPageHandler implements PageHandler{

	@SuppressWarnings("unchecked")
	public void handler(HtmlPage page) {
		page.submitLinksByReg("(https://([\\w\\-\\.]*)jd\\.com)");
		page.submitLinksByReg("(https://item\\.jd\\.com)/[0-9]+\\.html");
		List<Node> nodes = page.getContext().selectNodes("//DIV[@class='p-info lh']/DIV[@class='p-price']/STRONG/@class");
		for (Node node : nodes) {
			System.out.println(page.getHost().toString());
			System.out.println(node.getText());
		}
	}
	
	public static void main(String[] args) {
		
		Spider spider = new Spider().addPageHandler(new JDPageHandler()).domain("https://item.jd.com").threadSize(3);
		spider.execute();
	}
}
