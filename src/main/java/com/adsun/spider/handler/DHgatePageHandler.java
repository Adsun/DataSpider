package com.adsun.spider.handler;


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

import com.adsun.spider.core.HtmlPage;
import com.adsun.spider.core.Spider;
import com.adsun.spider.dao.DBHelper;
import com.adsun.spider.interfa.PageHandler;


public class DHgatePageHandler extends PageHandler{
	private static Log log = LogFactory.getLog(DHgatePageHandler.class);
	private static String keywords = "";
	private static DBHelper dao = DBHelper.getInstance();
	@SuppressWarnings({ "unchecked" })
	public void handler(HtmlPage page) {
//		System.out.println(page.getHost().toString());
		/*page.submitLinks("(https://([\\w\\-\\.]*)jd\\.com)");
		page.submitLinks("(https://item\\.jd\\.com)/[0-9]+\\.html");*/
		if (page.getHost().toString().contains(keywords)) {
			Object node1 = page.getContext().selectObject("//DIV[@class='page']/A[@class='next']/@href");
			log.info(page.getHost().toString());
			if (node1 instanceof DefaultAttribute) {
				page.submitLink(((DefaultAttribute)node1).getValue());
			}
			List<Node> nodes = page.getContext().selectNodes("//*[@id='proList']/DIV/H3/A/@href");
			for (Node node : nodes) {
				page.submitLink((node.getText().split("#")[0]).split("\\?")[0]);
			}
		} else if (page.getHost().getPath().contains("product") && page.getHost().getPath().endsWith("html")) {
			
			Object itemcode = page.getContext().selectObject("//*[@id='itemcode']/@value");
			Object supplierid = page.getContext().selectObject("//*[@id='supplierid']/@value");
			Node title = page.getContext().selectSingleNode("//*[@id='productdisplayForm']/DIV/DIV/DIV[1]/DIV/DIV[1]/H1");
			Node soldBy = page.getContext().selectSingleNode("//*[@id='loadSellerInformation_new']/DIV[1]/DIV[2]/A");
			Object soldByLink = page.getContext().selectObject("//*[@id='loadSellerInformation_new']/DIV[1]/DIV[2]/A/@href");
			String sql = "insert into item (supplier_id,item_code,link,title,sold_by,sold_by_link,created_datetime,updated_datetime) "
					+ " values(?,?,?,?,?,?,?,?)";
			dao.execute(sql, ((DefaultAttribute)supplierid).getValue() == null ? "" : ((DefaultAttribute)supplierid).getValue(),
							((DefaultAttribute)itemcode).getValue() == null ? "" : ((DefaultAttribute)itemcode).getValue(),
							page.getHost().getPath() == null ? "" : page.getHost().getPath(),
							title.getStringValue() == null ? "" : title.getStringValue(),
							soldBy.getStringValue() == null ? "" : soldBy.getStringValue(),
							((DefaultAttribute)soldByLink).getValue() == null ? "" : ((DefaultAttribute)soldByLink).getValue(),
							new Date(),new Date());
			
			StringBuffer uri = new StringBuffer("https://www.dhgate.com/product/inventory.do?version=1&client=pc");
			uri.append("&itemcode=" + ((DefaultAttribute)itemcode).getValue());
			uri.append("&productid=" + ((DefaultAttribute)supplierid).getValue());
			uri.append("&isdhporttype=");
			uri.append("&countryid=US");
			page.submitLink(uri.toString());
		} else if (page.getHost().getPath().contains("inventory.do")) {
			String itemcode = (page.getHost().toString().split("itemcode=")[1]).split("&supplierId")[0];
			String supplierid = (page.getHost().toString().split("productid=")[1]).split("&isdhporttype")[0];
			Element bodys = page.getContext().getRootElement();
			try {
				JSONObject jo = new JSONObject(bodys.getStringValue().replaceAll("\"\"", "\""));
				for (String key : jo.keySet()) {
					JSONObject obj = jo.getJSONObject(key);
					String skuId = obj.getString("a");
					String skuMd5 = obj.getString("g");
					JSONObject f = obj.getJSONObject("f");
					String productName = "";
					for (String fKey : f.keySet()) {
						productName = productName + " " + f.getString(fKey);
					}
					String sql = "insert into product_detail (supplier_id,item_code,sku_Id,sku_Md5,product_name,created_datetime,updated_datetime) "
						+ " values('"+supplierid+"','"+itemcode+"','"+skuId+"','"+skuMd5+"','"+productName+"',?,?)";
					dao.execute(sql, new Date(), new Date());
					StringBuffer uri = new StringBuffer("https://www.dhgate.com/product/productprict.do?version=1&client=pc");
					uri.append("&itemcode=" + itemcode);
					uri.append("&supplierId=" + supplierid);
					uri.append("&skuId=" + skuId);
					uri.append("&skuMd5=" + skuMd5);
					uri.append("&isdhporttype=");
					uri.append("&countryid=US");
					uri.append("&shippingtypeid=DHL");
					uri.append("&inventoryLocationCountryId=US");
					uri.append("&commtype=commissionNV2");
					uri.append("&isvipprod=0");
					uri.append("&isAttrClick=true");
					page.submitLink(uri.toString());
				}
			} catch (JSONException e) {
				
				log.error(bodys.getStringValue());
				e.printStackTrace();
			}
		} else if (page.getHost().getPath().contains("productprict.do")) {
//			System.out.println("itemcode = " + (page.getHost().toString().split("itemcode=")[1]).split("&supplierId")[0]);
			Element bodys = page.getContext().getRootElement();
			try {
				JSONObject jo = new JSONObject(bodys.getStringValue());
				JSONArray ja = jo.getJSONArray("productWholesaleRangeDTOList");
				for (int i = 0; i < ja.length(); i ++) {
					JSONObject joDtl = ja.getJSONObject(i);
					String sql = "update product_detail set discount=?,discount_Price=?,end_Qty=?,prom_Discount_Price=?,start_Qty=?,vip_Price=?,updated_datetime=?" 
							+ " where supplier_id=? and item_code=? and sku_Id=? and sku_Md5=? ";
					dao.execute(sql, joDtl.get("discount") == null || joDtl.get("discount") == "" ? 0 : joDtl.get("discount"),
							joDtl.get("discountPrice") == null || joDtl.get("discountPrice") == "" ? 0 : joDtl.get("discountPrice"),
							joDtl.get("endQty") == null || joDtl.get("endQty") == "" ? 0 : joDtl.get("endQty") == null,
							joDtl.get("promDiscountPrice") == null || joDtl.get("promDiscountPrice") == "" ? 0 : joDtl.get("promDiscountPrice"),
							joDtl.get("startQty") == null || joDtl.get("startQty") == "" ? 0 : joDtl.get("startQty") == null,
							joDtl.get("vipPrice") == null || joDtl.get("vipPrice") == "" ? 0 : joDtl.get("vipPrice") == null,
							new Date(),
							(page.getHost().toString().split("supplierId=")[1]).split("&skuId")[0] == null ? "" : (page.getHost().toString().split("supplierId=")[1]).split("&skuId")[0],
							(page.getHost().toString().split("itemcode=")[1]).split("&supplierId")[0] == null ? "" : (page.getHost().toString().split("itemcode=")[1]).split("&supplierId")[0],
							(page.getHost().toString().split("skuId=")[1]).split("&skuMd5")[0] == null ? "" : (page.getHost().toString().split("skuId=")[1]).split("&skuMd5")[0],
							(page.getHost().toString().split("skuMd5=")[1]).split("&isdhporttype")[0] == null ? "" : (page.getHost().toString().split("skuMd5=")[1]).split("&isdhporttype")[0]
							);
				}
			} catch (JSONException e) {
				log.error(bodys.getStringValue());
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		keywords = "screen" + "+" + "protector";
		Spider spider = new Spider().addPageHandler(new DHgatePageHandler()).domain("https://www.dhgate.com/wholesale/search.do?act=search&sus=&searchkey=screen+protector").threadSize(3);
		spider.execute();
	}
}
