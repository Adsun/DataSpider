package com.adsun.spider.interfa;

public interface LinkStorage {
	void addLink(String link);
	String getLink();
	boolean isEmpty();
}
