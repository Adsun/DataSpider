package com.adsun.spider.core;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.adsun.spider.util.LinkUtil;

public class HtmlPage {
	private Document context;
	private Header[] headers;
	private URI host;
	private List<String> links;
	private LinkStorage linkStorage;
	
	public HtmlPage(HttpResponse response, URI host, LinkStorage linkStorage){
		this.headers = response.getAllHeaders();
		this.host = host;
		this.linkStorage = linkStorage;
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(response.getEntity().getContent()));
			org.w3c.dom.Document w3cDoc = parser.getDocument();
			DOMReader reader = new DOMReader();
			setContext(reader.read(w3cDoc));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		setLinks(LinkUtil.getLinks(context, host));
	}
	
	/*��������ɸѡ ��Ҫ�ύ�����link*/
	public void submitLinksByReg(String regEx){
		for (String link : links) {
			if (Pattern.matches(regEx, link)) {
				linkStorage.addLink(link);
			}
		}
	}
	
	public void submitLink(String link){
		linkStorage.addLink(LinkUtil.parseLink(link, host));
	}

	public Document getContext() {
		return context;
	}

	public void setContext(Document context) {
		this.context = context;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public void setHost(URI host) {
		this.host = host;
	}

	public URI getHost() {
		return host;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public List<String> getLinks() {
		return links;
	}
}
