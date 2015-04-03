<%@page import="cn.wwerp.StatItem"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	String month = request.getParameter("month");
	String title = request.getParameter("Title"); 
	List<Item> list = wwService.getItems(month);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=title %>统计</title>
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
<div class="nav">当前位置：&nbsp;&nbsp;<%=title %>统计</div>
<div class="div_content">
<br/><br/>
<div align="center">
<form name="form1" id="mainForm" action="" method="post">
    <table class="table_content" style="width:90%;">
        <tbody>
        	<tr>
        		<th width="15%">日期</th>
        		<th width="15%">备用金</th>
        		<th width="15%">收入</th>
        		<th width="15%">支出</th>
        		<th width="15%">结余</th>
        		<th >&nbsp;</th>
        	</tr>
        <% 
        	int incoming = 0;
        	int outgoing = 0;
        	int remain = (int)(list.isEmpty() ? 0 : list.get(0).RemainAmount);
        	for (Item item : list) {
        		incoming += item.TotalIncoming;
        		outgoing += item.TotalOutgoing;
        		remain += item.TotalIncoming - item.TotalOutgoing;
        		%>
            <tr height="30px" class="table_content_line">
                <td>&nbsp;<%=item.ItemDate %>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(item.PrepareAmount)%>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(item.TotalIncoming)%>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(item.TotalOutgoing)%>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(item.TotalIncoming - item.TotalOutgoing)%>&nbsp;</td>
				<td>
					<a href="itemdetail.jsp?Id=<%=item.Id %>">【明细】</a>
					<% if (item.getCreateTime().getTime() + 86400000L > System.currentTimeMillis()) { %>	
					<a href="additem.jsp?date=<%=item.ItemDate%>&Id=<%=item.Id %>&act=update">【修改】</a>
					<%} else { %>
					【修改】
					<%} %>
				</td>
			</tr>
		<%} %>
		<tr height="30px">
                <td>&nbsp;合计&nbsp;</td>
                <td>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(incoming) %>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(outgoing) %>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(remain) %>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
        </tbody>
    </table>
</form>
 </div>
</div>
</body>
</html>
