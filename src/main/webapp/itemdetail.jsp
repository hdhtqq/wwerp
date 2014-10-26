<%@page import="java.util.Date"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%	
	Item item = wwDao.queryObject(Item.class, Util.str2int(request.getParameter("Id")));
	String sql = "select a.* from ItemDetail a, ItemType b where a.ItemId=? and a.TypeId=b.Id and b.Type=?";
	List<ItemDetail> incomings = wwService.getItemDetails(item.Id, ItemType.INCOMINGS);
	List<ItemDetail> outgoings = wwService.getItemDetails(item.Id, ItemType.OUTGOINGS);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>每日账单明细</title>
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
<div class="nav">当前位置：&nbsp;&nbsp;每日账单明细&nbsp;<%=item.ItemDate %></div>
<div class="div_content">
<br/><br/>
<div align="center">
	<table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>日期：</strong></td>
        		<td style="text-align: left;"><%=item.ItemDate %></td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>备用金：</strong></td>
        		<td style="text-align: left;"><%=Util.trimNumber(item.PrepareAmount) %>&nbsp;元</td>
        	</tr>
        </tbody>
     </table>
     	
     <br/>
      
     <div style="height: 40px;width:70%; text-align: left;">
		<strong>营业收入</strong>
		<hr width="100%" size="1" color="#666" align="center" noshade="noshade" />
	 </div>
	 
	 <table class="table_content" style="width: 70%;">
        <tbody>
        	<tr>
        		<th width="10%">序号</th>
        		<th width="20%">项目</th>
        		<th width="10%">单价</th>
        		<th width="10%">数量</th>
        		<th width="10%">总金额</th>
        		<th>备注</th>
        	</tr>
        <% 
        	for (int idx=0; idx<incomings.size(); idx++) {
        		ItemDetail d = incomings.get(idx);
        		ItemType type = wwService.getItemType(d.TypeId);
        		%>
            <tr height="30px">
                <td>&nbsp;<%=idx+1 %>&nbsp;</td>
				<td>&nbsp;<%=type.Name %>&nbsp;</td>
				<td><%=Util.trimNumber(d.Price) %>&nbsp;元</td>
				<td>&nbsp;&nbsp;<%=Util.trimNumber(d.Quantity) %>&nbsp;<%=type.Unit %></td>
				<td><%=Util.trimNumber(d.Amount) %>&nbsp;元</td>
				<td>&nbsp;<%=d.Remark %>&nbsp;</td>
			</tr>
		<%} %>
		<tr height="30px">
                <td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;合计&nbsp;</td>
				<td>
					<span id="SpanTotalIncoming"><%=Util.trimNumber(item.TotalIncoming) %></span>&nbsp;元
				</td>
				<td>&nbsp;<%=item.IncomingRemark %>&nbsp;</td>
			</tr>
        </tbody>
    </table>
     
     <br/><br/>
     <div style="height: 40px;width:70%; text-align: left;">
		<strong>支出</strong>
		<hr width="100%" size="1" color="#666" align="center" noshade="noshade" />
	 </div>
  
  	<table class="table_content" style="width: 70%;">
        <tbody>
        	<tr>
        		<th width="10%">序号</th>
        		<th width="20%">项目</th>
        		<th width="10%">单价</th>
        		<th width="10%">数量</th>
        		<th width="10%">总金额</th>
        		<th>备注</th>
        	</tr>
        <% 
        	for (int idx=0; idx<outgoings.size(); idx++) {
        		ItemDetail d = outgoings.get(idx);
        		ItemType type = wwService.getItemType(d.TypeId);
        		%>
            <tr height="30px">
                <td>&nbsp;<%=idx+1 %>&nbsp;</td>
				<td>&nbsp;<%=type.Name %>&nbsp;</td>
				<td><%=Util.trimNumber(d.Price) %>&nbsp;元</td>
				<td>&nbsp;&nbsp;<%=Util.trimNumber(d.Quantity) %>&nbsp;<%=type.Unit %></td>
				<td><%=Util.trimNumber(d.Amount) %>&nbsp;元</td>
				<td>&nbsp;<%=d.Remark %>&nbsp;</td>
			</tr>
		<%} %>
		<tr height="30px">
                <td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;合计&nbsp;</td>
				<td>
					<span id="SpanTotalOutgoing"><%=Util.trimNumber(item.TotalOutgoing) %></span>&nbsp;元
				</td>
				<td>&nbsp;<%=item.OutgoingRemark %>&nbsp;</td>
			</tr>
        </tbody>
    </table>
    
    <br/>
    
    <table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>结余：</strong></td>
        		<td style="text-align: left;">
        			<span id="SpanRemainAmount"><%=Util.trimNumber(item.RemainAmount) %></span>&nbsp;元
        		</td>
        	</tr>
        </tbody>
     </table>
 </div>
</div>
</body>
</html>
