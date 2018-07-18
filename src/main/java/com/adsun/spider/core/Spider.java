package com.adsun.spider.core;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.adsun.spider.interfa.LinkStorage;
import com.adsun.spider.interfa.PageHandler;
import com.adsun.spider.interfa.SpiderMBean;
import com.adsun.spider.util.SpiderThreadPoolUtil;


public class Spider implements SpiderMBean{
	private static Log log = LogFactory.getLog(Spider.class);
	
	private final static int STATUS_STOP = 0;
	private final static int STATUS_RUNNING = 1;
	private static int STATUS = STATUS_STOP;
	
	private int threadSize = 3;
	
	private List<PageHandler> handlerList = new ArrayList<PageHandler>();
	
	private List<String> domainList = new ArrayList<String>();
	
	private PoolingHttpClientConnectionManager cm = null;
	
	private CookieStore cookieStore = new BasicCookieStore();
	
	private LinkStorage linkStorage = new LinkBlockingQueue();
	
	private static ThreadPoolExecutor threadPoolExecutor = SpiderThreadPoolUtil.getInstance();
	
	final CloseableHttpClient client = HttpClients.custom()
	.setConnectionManager(cm)
	.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build())
	.setDefaultCookieStore(cookieStore)
	.build();
	
	public Spider addPageHandler(PageHandler pageHandler) {
		handlerList.add(pageHandler);
		return this;
	}
	
	public Spider domain(String domain) {
		domainList.add(domain);
		return this;
	}
	
	public Spider threadSize(int size) {
		this.threadSize = size;
		return this;
	}
	
	public Spider cookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		return this;
	}
	
	public void execute() {
		for (String link : domainList) {
			linkStorage.addLink(link);
		}
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(threadSize);
		this.registerMBean();
		this.start();
		this.running();
	}
	
	/**
	 * 添加jconsole监控
	 */
	private void registerMBean() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        try {
        	ObjectName name = new ObjectName("io.spider.core:type=Spider");  
			mbs.registerMBean(this, name);
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}  
	}
	
	public void stop() {
		if (STATUS != STATUS_STOP) {
			STATUS = STATUS_STOP;
			log.info("******************STOPPED******************");
		}
	}
	
	public void start() {
		if (STATUS != STATUS_RUNNING) {
			STATUS = STATUS_RUNNING;
			log.info("******************RUNNING******************");
		}
	}
	
	private void running() {
		
		while (true) {
			if (STATUS == STATUS_STOP) {
				System.exit(0);
				continue;
			}
			if (linkStorage.isEmpty()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (linkStorage.isEmpty()) {
					STATUS = STATUS_STOP;
				}
				continue;
			}
			final String link = linkStorage.getLink();
			Runnable run = new Runnable() {
				public void run() {
					CloseableHttpResponse response = null;
					try {
						HttpGet get = new HttpGet(link);
						response = client.execute(get);
						HtmlPage page = new HtmlPage(response, get.getURI(), linkStorage);
						if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
							for (PageHandler pageHandler : handlerList) {
								pageHandler.handler(page);
							}
						}
						response.close();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						log.error(e);
					} catch (IOException e) {
						e.printStackTrace();
						log.error(e);
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e);
					}
				}
			};
			threadPoolExecutor.submit(run);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://github.com/code4craft");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			System.out.println(EntityUtils.toString(entity));
			log.info(response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				response.close();
				client.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
