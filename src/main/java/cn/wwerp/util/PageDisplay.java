package cn.wwerp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.wwerp.JspUtil;

public class PageDisplay {
	
	public static final String CURPAGE_ID = "p_cur";
	public static final String PAGE_NUM = "p_num";
	
	private String pageUrl;
	private int totalRecordCount;
	private int recordCountPerPage;
	private int totalPageCount;
	private int curPage;
	
	private int pageLinkBegin;
	private int pageLinkEnd;
	
	public PageDisplay(HttpServletRequest request, HttpServletResponse response, int totalRecordCount, int recordCountPerPage, int pageLinkCount) {
		this.totalRecordCount = totalRecordCount;
		
		this.recordCountPerPage = getRecordCountPerPage(request, response, recordCountPerPage);
		totalPageCount = totalRecordCount/this.recordCountPerPage;
		if (totalRecordCount%this.recordCountPerPage > 0)
			totalPageCount++;
		
		curPage = str2int(request.getParameter(CURPAGE_ID), 1); 
			
		pageLinkBegin = 1;
		if (curPage > pageLinkCount / 2)
			pageLinkBegin = curPage - pageLinkCount / 2;
		pageLinkEnd = pageLinkBegin + pageLinkCount - 1;
		if (pageLinkEnd > totalPageCount) {
			pageLinkEnd = totalPageCount;
			pageLinkBegin = pageLinkEnd - pageLinkCount + 1;
			if (pageLinkBegin < 1)
				pageLinkBegin = 1;
		}
		  
		pageUrl = request.getServletPath() + "?" + removeRequestParamNameValue(request.getQueryString(), new String[]{CURPAGE_ID, PAGE_NUM});
		
		request.setAttribute(PageDisplay.class.getName(), this);
	}
	
	private int getRecordCountPerPage(HttpServletRequest request, HttpServletResponse response, int defaultCount) {
		int count = Util.str2int(request.getParameter(PAGE_NUM));
		if (count > 0) {
			Cookie c = new Cookie(PAGE_NUM, String.valueOf(count));
			c.setMaxAge(Integer.MAX_VALUE);
			response.addCookie(c);
		} else {
			count = Util.str2int(JspUtil.getCookie(request, PAGE_NUM), defaultCount);
		}
		return count;
	}
	
	private static String removeRequestParamNameValue(String urlOld, String[] names) {
		for (String name : names) {
			urlOld = removeRequestParamNameValue(urlOld, name);
		}
		return urlOld;
	}
	
	private static String removeRequestParamNameValue(String urlOld, String name) {
		if (urlOld == null)
			return "";
		
		int pos1 = urlOld.indexOf("?" + name + "=");
		if (pos1 < 0)
			pos1 = urlOld.indexOf("&" + name + "=");
		if (pos1 >= 0) {
			String url = urlOld.substring(0, pos1);
			int pos2 = urlOld.indexOf('&', pos1 + name.length() + 2);
			if (pos2 > 0)
				url += urlOld.substring(pos2);
			return url;
		} else {
			return urlOld;
		}
	}
	
	public void filterRequestParamName(String name) {
		pageUrl = removeRequestParamNameValue(pageUrl, name);
	}
	
	private int str2int(String s, int defaultValue) {
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}
	
	public <T> Collection<T> getCurPageRecords(Collection<T> allRecords) {
		if (allRecords == null || allRecords.size() <= recordCountPerPage)
			return allRecords;
		
		if (allRecords instanceof List)
			return getCurPageRecords((List<T>)allRecords);
		
		int idxBegin = recordCountPerPage * (curPage - 1);
		List<T> list = new ArrayList<T>();
		int i = 0;
		for (T obj : allRecords) {
			if (i >= idxBegin)
				list.add(obj);
			i++;
			if (list.size() >= recordCountPerPage)
				break;
		}
		return list;
	}
	
	public <T> List<T> getCurPageRecords(List<T> allRecords) {
		if (allRecords == null || allRecords.size() <= recordCountPerPage)
			return allRecords;

		int idxBegin = recordCountPerPage * (curPage - 1);
		if (idxBegin >= allRecords.size())
			idxBegin = allRecords.size() - 1;
		int idxEnd = idxBegin + recordCountPerPage;
		if (idxEnd >= allRecords.size())
			idxEnd = allRecords.size();
		return allRecords.subList(idxBegin, idxEnd);
	}

	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	public int getRecordCountPerPage() {
		return recordCountPerPage;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public int getCurPage() {
		return curPage;
	}
	
	public int getIndexOfAllRecord() {
		return recordCountPerPage * (curPage - 1) + 1;
	}
	
	public int getSqlBegin() {
		return recordCountPerPage * (curPage - 1);
	}
	
	public int getSqlNum() {
		return recordCountPerPage;
	}
	
	public String getCurPageLink(String[] ignoreParams) {
		String url = pageUrl;
		if (ignoreParams != null) {
			for (String name : ignoreParams) {
				url = removeRequestParamNameValue(url, name);				
			}
		}
		return url + "&p_cur=" + this.curPage;
	}
	
	public String getCurPageLink() {
		return getCurPageLink(null) ;
	}
	
	public String getFirstPageLink() {
		return pageUrl + "&p_cur=1";
	}
	
	public String getLastPageLink() {
		return pageUrl + "&p_cur=" + totalPageCount;
	}
	
	public String getPrePageLink() {
		if (curPage > 1)
			return pageUrl + "&p_cur=" + (curPage - 1);
		else
			return "#";
	}
	
	public String getNextPageLink() {
		if (curPage < totalPageCount)
			return pageUrl + "&p_cur=" + (curPage + 1);
		else
			return "#";
	}
	
	public boolean hasPrePage() {
		return (curPage > 1);
	}
	
	public boolean hasNextPage() {
		return (curPage < totalPageCount);
	}
	
	public boolean isCurPage(int page) {
		return (curPage == page);
	}
	
	public int getPageLinkBegin() {
		return pageLinkBegin;
	}

	public int getPageLinkEnd() {
		return pageLinkEnd;
	}
	
	public String getPageLink(int i) {
		return pageUrl + "&p_cur=" + i;
	}

	public String getPageUrl() {
		return pageUrl;
	}

}
