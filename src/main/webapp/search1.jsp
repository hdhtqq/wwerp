<%@page import="java.util.Comparator"%>
<%@page import="cn.wwerp.TypeClass"%>
<%@page import="java.util.Date"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	int classId = Util.str2int(request.getParameter("ClassId"));
	List<ItemType> types = wwService.getTypesByClassId(classId);
	Collections.sort(types, new Comparator<ItemType>() {
	    public int compare(ItemType o1, ItemType o2) {
	    	if (o1.Type == o2.Type) {
	    		return o1.Id - o2.Id;
	    	} else {
	    		return o1.Type - o2.Type;
	    	}
	    }
	});
	List<Item> items = null;
	String date1 = request.getParameter("Date1");
	String date2 = request.getParameter("Date2");
	int typeId = Util.str2int(request.getParameter("TypeId"));
	if (date1 != null) {
		String sql = "select b.ItemDate, sum(a.Amount) TotalIncoming from ItemDetail a, Item b "
						+ "where a.ItemId=b.Id and b.ItemDate>=? and b.ItemDate<=? and a.TypeId=? " 
						+ " group by b.ItemDate order by ItemDate desc";
		items = wwDao.queryList(Item.class, sql, new Object[]{date1, date2, typeId});
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>分类搜索</title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css">
<script src="/js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="/js/My97DatePicker/WdatePicker.js"></script>
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
<form name="form1" id="mainForm" action="" method="post">
	<table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>开始日期：</strong></td>
        		<td style="text-align: left;"><input type="text" name="Date1" size="12" onclick="WdatePicker()" value="<%=b(date1) %>" /></td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>结束日期：</strong></td>
        		<td style="text-align: left;"><input type="text" name="Date2" size="12" onclick="WdatePicker()" value="<%=b(date2) %>" /></td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>分类：</strong></td>
        		<td style="text-align: left;">
        			<select name="TypeId">
        				<% for (ItemType t : types) { %>
        				<option value="<%=t.Id %>"><%=(t.Type == 1 ? "收入-" : "支出-") +  t.Name %></option>
        				<%} %>
        			</select>
        		</td>
        	</tr>
        </tbody>
     </table>
     	
    <br/>
    <div style="text-align: center;">
     	<input type="hidden" value="<%=classId %>" name="ClassId" /> 
    	<input type="submit" value=" 搜索 " />&nbsp;&nbsp;&nbsp;&nbsp;
    </div>
    <br/><br/>
    
    <% 
        if (items != null) { %>
    <table class="table_content" style="width: 70%;">
        <tbody>
        	<tr>
        		<th width="20%">日期</th>
        		<th width="10%">金额</th>
        		<th>备注</th>
        	</tr>
        <% 
        	for (Item d : items) {
        		%>
            <tr height="30px">
				<td>&nbsp;<%=d.ItemDate %>&nbsp;</td>
				<td><%=Util.trimNumber(d.TotalIncoming) %>&nbsp;元</td>
			</tr>
		<%} %>
        </tbody>
    </table>
    <%} %>
</form>
 </div>
</div>
</body>
</html>
