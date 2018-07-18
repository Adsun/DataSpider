package com.adsun.spider.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Node;

public class LinkUtil {
	@SuppressWarnings("unchecked")
	public static List<String> getLinks(Document document, URI host) {
		List<Node> links = document.selectNodes("//@href");
		return parseLinks(links, host);
	}
	
	public static List<String> parseLinks(List<Node> links, URI host) {
		List<String> newLinks = new ArrayList<String>();
		for (Node link : links) {
			String newLink = parseLink(link.getText(), host);
			if (newLink != null) {
				newLinks.add(newLink);
			}
		}
		return newLinks;
	}
	
	public static String parseLink(String link, URI host) {
	    //  //baidu.com
	    String regEx1 = "\\/\\/([^/:]+)\\.([^/:]+)([\\/\\S]*)";
	    //  /baidu.com
	    String regEx2 = "\\/([^/:]+)([\\/\\S]*)";
	    // http://baidu.com
	    String regEx3 = "(\\w+):\\/\\/([^/:]+)\\.([^/:]+)([\\/\\S]*)";
	    
	    if (Pattern.matches(regEx1, link)) {
	    	return host.getScheme() + ":" + link;
	    } else if (Pattern.matches(regEx2, link)) {
	    	return host.getScheme() + "://" + host.getHost() + link;
	    } else if (Pattern.matches(regEx3, link)) {
	    	return link;
	    }
		return null;
	}
}
