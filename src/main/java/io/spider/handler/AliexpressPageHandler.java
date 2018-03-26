package io.spider.handler;


import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.spider.core.HtmlPage;
import io.spider.core.Spider;
import io.spider.dao.DBHelper;


public class AliexpressPageHandler implements PageHandler{
	private static Log log = LogFactory.getLog(AliexpressPageHandler.class);
	private static String keywords = "";
//	private static DBHelper dao = DBHelper.getInstance();
	public void handler(HtmlPage page) {
		System.out.println(page.getContext().asXML());
	}
	
	public static void main(String[] args) {
		keywords = "screen" + "+" + "protector";
		Spider spider = new Spider().addPageHandler(new AliexpressPageHandler()).domain("https://www.aliexpress.com/wholesale?SearchText=screen+protector").threadSize(3);
		spider.execute();
	}
}
