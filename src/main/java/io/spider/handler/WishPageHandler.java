package io.spider.handler;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.spider.core.HtmlPage;
import io.spider.core.Spider;
import io.spider.dao.DBHelper;


public class WishPageHandler implements PageHandler{
	private static Log log = LogFactory.getLog(WishPageHandler.class);
	private static String keywords = "";
//	private static DBHelper dao = DBHelper.getInstance();
	public void handler(HtmlPage page) {
		System.out.println(page.getContext().asXML());
	}
	
	public static void main(String[] args) {
		keywords = "screen protector";
		Spider spider = new Spider().addPageHandler(new WishPageHandler()).domain("https://www.wish.com/search/screen%20protector").threadSize(3);
		spider.execute();
	}
}
