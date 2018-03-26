package io.spider.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpiderThreadPoolUtil {
	private static ThreadPoolExecutor executor = null;
	private SpiderThreadPoolUtil () {
	}
	public static ThreadPoolExecutor getInstance() {
		if (executor == null) {
			executor = new ThreadPoolExecutor(5, 10, 1000,TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(500), new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return executor;
	}
}
