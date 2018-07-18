package com.adsun.spider.core;

public interface LinkStorage {
	void addLink(String link);
	String getLink();
	boolean isEmpty();
}
