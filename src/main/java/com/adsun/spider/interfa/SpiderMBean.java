package com.adsun.spider.interfa;

/**
 * 继承这个接口，可以在jconsole中操作stop和start
 * @author Administrator
 *
 */
public interface SpiderMBean {
	void stop();
	void start();
}
