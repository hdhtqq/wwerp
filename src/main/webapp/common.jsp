<%@page import="cn.wwerp.WWDao"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cn.wwerp.util.Constants"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="org.springframework.web.util.HtmlUtils"%>
<%@page import="cn.wwerp.BaseBean"%>
<%@page import="java.util.concurrent.atomic.AtomicLong"%>
<%@page import="cn.wwerp.util.Util"%>
<%@page import="cn.wwerp.WWService"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page contentType="text/html; charset=utf-8" language="java"%>

<%!String n(Object obj) {
		if (obj == null)
			return "&nbsp;";
		String s = String.valueOf(obj).trim();
		if (s.isEmpty())
			return "&nbsp;";
		return HtmlUtils.htmlEscape(s);
	}

	String b(Object obj) {
		if (obj == null)
			return "";
		return String.valueOf(obj).trim();
	}
	%>
<%
	String loginUid = (String)session.getAttribute(Constants.SESSION_ATTR_USER);
	if (loginUid == null) {
		response.sendRedirect("/logout.jsp");
		return; 
	}
	WebApplicationContext ctx = (WebApplicationContext) application.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
	final WWService wwService = ctx.getBean("wwService", WWService.class);
	final WWDao wwDao = ctx.getBean("wwDao", WWDao.class);
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
%>

