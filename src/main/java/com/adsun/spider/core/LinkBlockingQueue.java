package com.adsun.spider.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.adsun.spider.interfa.LinkStorage;

public class LinkBlockingQueue implements LinkStorage{
	private final static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100000);
	private final static List<String> list = new ArrayList<String>();
	private final static Long lock = 1L;
	
	public void addLink(String link) {
		synchronized (lock) {
			if (!queue.contains(link) && !list.contains(link)) {
				queue.offer(link);
			}
		}
	}
	
	public String getLink() {
		synchronized (lock) {
			String link = queue.poll();
			if (link != null) {
				list.add(link);
			}
			return link;
		}
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
}
