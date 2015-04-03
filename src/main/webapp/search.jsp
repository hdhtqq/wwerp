<%@page import="cn.wwerp.TypeClass"%>
<%@page import="java.util.Date"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	List<TypeClass> cs = wwService.getTypeClasses();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>分类搜索</title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css">
<script src="/js/jquery-1.9.1.js"></script>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"></script>
<script src="/js/yx.js"></script>
<script>
$(function() {
	YX.globalInit();
});

</script>
</head>
<body>
<div class="nav">当前位置：&nbsp;&nbsp;分类搜索</div>
<div class="div_content">
<br/><br/>
<div align="center">
<form name="form1" id="mainForm" action="search1.jsp" method="post">
	<table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>分类：</strong></td>
        		<td style="text-align: left;">
        			<select name="ClassId">
        				<% for (TypeClass c : cs) { %>
        				<option value="<%=c.Id %>"><%=c.Name %></option>
        				<%} %>
        			</select>
        		</td>
        	</tr>
        </tbody>
     </table>
     	
    <br/>
    <div style="text-align: center;">
    	<input type="submit" value=" 下一步 " />&nbsp;&nbsp;&nbsp;&nbsp;
    </div>
    <br/><br/>
</form>
 </div>
</div>
</body>
</html>
