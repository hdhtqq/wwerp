<%@page import="cn.wwerp.util.Constants"%>
<%@page import="cn.wwerp.util.Util"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
	String text = request.getParameter("Text");
	int type = Util.str2int(request.getParameter("Type"));
	if (text == null) {
		boolean success = ("true".equalsIgnoreCase(request.getParameter("Success")));
		if (success) {
			if (type == Constants.ACT_TYPE_ADD)
				text = "添加成功";
			else if (type == Constants.ACT_TYPE_MODIFY)
				text = "修改成功";
			else if (type == Constants.ACT_TYPE_DELETE)
				text = "删除成功";
			else
				text = "操作成功";	
		} else {
			text = "操作失败，请和管理员联系";
		}
	}
	
	String backurl = request.getParameter("BackUrl");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=text %></title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css"/>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-1.9.1.js"></script>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"></script>
<style>
  body{
 width: 100%;
 height: 100%;
  }
</style>

<script>
$(function(){	
	$("#returnBack").button();
});
	function goback(){
		window.location = "<%=backurl%>";
	}
</script>

</head>
<body>
<br/>
<center>
	<br/><br/>
	<p><font size="28"><%=text %></font></p>
	<br/><br/>
	<p>
		<% if (backurl != null) { %>
	      	<input type="button"  id="returnBack" value="返 回 " onclick="goback();"/>
	      <% } %>
	</p>
</center>
</body>
</html>
