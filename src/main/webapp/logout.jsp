<%@page import="cn.wwerp.util.Constants"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	session.removeAttribute(Constants.SESSION_ATTR_USER);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>退出登陆</title>  
</head>
<body>
	 <script type="text/javascript">
	 	parent.window.location = "/login.jsp";
	 </script>
</body>
</html>