package com.adsun.spider.interfa;

import com.adsun.spider.core.HtmlPage;
import com.adsun.spider.core.Spider;

public abstract class PageHandler {
	public static Spider spider = new Spider();
	public void stop() {
		spider.stop();
	}
	public void start() {
		spider.start();
	}
	public abstract void handler(HtmlPage page);
}
