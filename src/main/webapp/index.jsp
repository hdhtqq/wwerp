<%@page import="java.util.Map"%>
<%@page import="cn.wwerp.util.Constants"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="common.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name='viewport' content='width=device-width,user-scalable=no'/>
<title>小掌柜收支管理系统</title>
<link rel="stylesheet"href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css"/>
<script src="/js/jquery-1.9.1.js"></script>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"></script>
<style type="text/css">
* {
	padding: 0px;
	margin: 0px;
} 
html {
	overflow-x:hidden;
	height: 100%;
} 
.ui-accordion-content{ zoom: 1; } 

img {
	behavior: url(/iepngfix/iepngfix.htc); 
	border: 0;
}


body {
	font: 12px Arial, Helvetica, sans-serif;
	color: #000;
	background-color: #FFFFFF;
	position: relative;	
	margin: 0;
	width: 100%;
	height: 100%;
	background-image: url(about:blank); /* for IE6 */
	background-attachment: fixed; /*必须*/
}
#container {
	width: 100%;
	height: 100%;
}

#menu_box {
	border-right: 5px solid #0489c6;
	padding: 5px;
}

table {
	font-size: 12px;
}
.menu_1 {
	padding-left:20px;
	line-height: 26px;
	color: #333333;
	list-style-type: none;
	display: block;
	height: 25px;
	width: 120px;
	cursor: default;
}

.menu_2 {
	padding-left:20px;
}
.menu_2 li {
	font-size: 12px;
	line-height: 25px;
	color: #333333;
	list-style-type: none;
	display: block;
	text-decoration: none;
	height: 25px;
	width: 120px;
	padding-left: 17px;
}

.ui-draggable,.ui-droppable {
	background-position: top;
}

a {
	text-decoration: none;
	color: #5584a0;
}
a:hover {
	color: #FF0000;
}
</style>
<script type="text/javascript">
	var old_click_link;
	$(function() {
		$("#accordion").accordion({
			heightStyle : "content" //fill
		});

		$("li a").click(function() {
			if (old_click_link)
				old_click_link.css("color", "");
			$(this).css("color", "#00F");
			old_click_link = $(this);
		});
		//if (jQuery.browser.msie && jQuery.browser.version.substr(0,1)<7)
		if(navigator.userAgent.indexOf("MSIE")>0)
		    jQuery('#accordion *').css('zoom', '1');
	}); 
</script>  
</head>
<body>
<table width="100%" height="100%">
		<tr>
			<td colspan="2" height="10%">
				  <table width="100%" border="0" cellpadding="0" cellspacing="0" style="background-image: url('img/title_bg.png'); height: 80px; border: 0px;">
						 <tr>
							    <td width="30%"><h1>&nbsp;&nbsp;&nbsp;&nbsp;小掌柜收支管理系统</h1></td>
							    <td width="35%" style="text-align:center;"></td>
							    <td width="35%" valign="bottom" style="padding-bottom:15px; text-align:right; padding-right:30px;">	    
										&nbsp;&nbsp;&nbsp;<a href="logout.jsp">【退出】</a>
							   </td>
						 </tr>
	             </table>
	        </td>		
		</tr>
		<tr >
		<%
			String  width = "200";
			String agent = request.getHeader("User-Agent");
			if (agent.indexOf("MSIE") != -1) { //IE浏览器
				width = "8%";
			} 
		%>
			<td id="menu_box" valign="top" width="<%=width%>">
				<div id="container">
					<div id="accordion">
						<h3>菜单</h3>
						<div>
							<div class="menu_1">
								<a href="additem.jsp" target="main">每日账单</a>
							</div>
							<div class="menu_1">
								<a href="types.jsp" target="main">分类管理</a>
							</div>
							<div class="menu_1">
								月份统计
							</div>
							<div class="menu_2">
								<ul>
								<%
								Map<String, String>  months = wwService.getStatMonths();
								for (Map.Entry<String, String> entry : months.entrySet()) {
								%>
									<li><a href="monthstats.jsp?month=<%=entry.getKey() %>&Title=<%=entry.getValue() %>" target="main"><%=entry.getKey() %></a></li>
								<%} %>
								</ul>								
							</div>
						</div>
					</div>
				</div>
			</td>
			<td valign="top">
				<iframe  id="main" name="main" width="100%" height="100%" frameborder="0" scrolling="auto" style="overflow:visible;" marginheight="0" marginwidth="0"  src="" ></iframe>
			</td>
		</tr>
	</table>
</body>
</html>