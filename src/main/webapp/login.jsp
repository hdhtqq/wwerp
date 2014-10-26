<%@page import="java.util.Map"%>
<%@page import="cn.wwerp.util.Constants"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%
	String errMsg = "";

	String user = request.getParameter("user");
	String pwd = request.getParameter("pwd");
	if (user != null && pwd != null) {
		if ("xiaozhanggui".equals(user) && "88888888".equals(pwd)) {
			session.setAttribute(Constants.SESSION_ATTR_USER, "admin");
			response.sendRedirect("index.jsp");
			return;
		}
		errMsg = "用户名或密码错误";
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name='viewport' content='width=device-width,user-scalable=no'/>
<title>小掌柜收支管理系统</title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css">
<script src="/js/jquery-1.9.1.js"></script>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"></script>
<script src="/js/yx.js"></script>
<style type="text/css">
</style>
<script type="text/javascript">
$(function() {
	YX.globalInit();
});

function trimStr(str) {
	var reg = /(^\s+)|(\s+$)/g;
	return str.replace(reg, "");
}

function doLogin() {
	if (trimStr(form1.user.value) == "") {
		alert("请输入用户名");
		form1.user.focus();
		return false;
	}
	if (trimStr(form1.pwd.value) == "") {
		alert("请输入密码");
		form1.pwd.focus();
		return false;
	}
	form1.submit();
}
</script>  
</head>
<body>
<div class="div_content" style="text-align: center;">
<div style="width:100%;height:80px;background-image: url('img/title_bg.png'); text-align: left;font-size: 24px;">
<br/>
<strong>小掌柜收支管理系统</strong>
</div>
<br/><br/><br/><br/><br/><br/><br/>
<div style="color: red;height: 30px;"><%=errMsg %></div>
<form name="form1" method="post" action="">
<table align="center" border="0" style="text-align: center;width: 40%;">
					<tr height="50"><td colspan="2" align="center"><strong>用户登录</strong></td></tr>
					<tr>
						<td width="45%" style="text-align: right;">用户名：</td>
						<td  style="text-align: left;"><input type="text" name="user" size="20" /></td>
					</tr>
					<tr>
						<td align="right" style="text-align: right;">密码&nbsp;&nbsp;：</td>
						<td  style="text-align: left;"><input type="password" name="pwd" size="20" /></td>
					</tr>
					<tr height="50"><td colspan="2" align="center"><input type="button" value=" 登 录 " onclick="doLogin();" /></td></tr>
				</table>
</form>
</div>
</body>
</html>