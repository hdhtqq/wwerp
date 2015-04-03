<%@page import="cn.wwerp.TypeClass"%>
<%@page import="java.util.Date"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	List<ItemType> types = wwService.getTypes();
	List<ItemType> typesIncomings = wwService.getTypes(ItemType.INCOMINGS);
	List<ItemType> typesOutgoings = wwService.getTypes(ItemType.OUTGOINGS);
	
	String incomingRemark = request.getParameter("IncomingRemark");
	if (incomingRemark != null) {
		Item item = new Item();
		Util.fillBean(item, request);
		
		if (item.ItemDate.trim().isEmpty()) {
			response.sendRedirect("/result.jsp?Success=false"
					+ "&Text=" + Util.urlEncode("保存失败！日期不能为空！"));
			return;
		}
		
		item.ItemDate = item.ItemDate.trim();
		String[] ss = item.ItemDate.split("-");
		item.ItemDate = ss[0] + "-" + (ss[1].length() == 1 ? "0" : "") + ss[1] + "-" + (ss[2].length() == 1 ? "0" : "") + ss[2];
		
		int count = wwDao.queryCount("select count(*) from Item where ItemDate=? and Id<>?", new Object[]{item.ItemDate, item.Id});
		if (count > 0) {
			response.sendRedirect("/result.jsp?Success=false"
					+ "&Text=" + Util.urlEncode("保存失败！" + item.ItemDate + "的数据已存在，请从系统统计中选择修改"));
			return;
		}
		
		List<Integer> delDetailIds = new ArrayList<Integer>();
		List<ItemDetail> details = new ArrayList<ItemDetail>();
		for (int i=0; i<types.size(); i++) {
			ItemType type = types.get(i);
			String price = request.getParameter("Price_" + i);
			String quantity = request.getParameter("Quantity_" + i);
			int detailId = Util.str2int(request.getParameter("Id_" + i));
			if (!Util.isStrEmpty(price) && !Util.isStrEmpty(quantity)) {
				ItemDetail d = new ItemDetail();
				d.setId(detailId); 
				d.setTypeId(Util.str2int(request.getParameter("TypeId_" + i))); 
				d.setPrice(Util.str2float(price, 0)); 
				d.setQuantity(Util.str2float(quantity, 0));
				d.setAmount(Util.str2float(request.getParameter("Amount_" + i), 0));
				d.setRemark(request.getParameter("Remark_" + i));
				
				details.add(d);
			}
			
			if (detailId > 0 && Util.isStrEmpty(quantity)) {
				delDetailIds.add(detailId);
			}
		}
		if (!delDetailIds.isEmpty())
			wwService.deleteItemDetails(item.Id, delDetailIds);
		
		wwService.saveItem(item, details);  
		response.sendRedirect("/result.jsp?Success=true");
		return;
	}
	
	Item item = null;
	int id = Util.str2int(request.getParameter("Id"));
	if (id > 0) {
		item = wwDao.queryObject(Item.class, id);
	} else {
		item = new Item();
		item.ItemDate = Util.formatDate(new Date(), "yyyy-MM-dd");
	}
	int i = 0;
	
	String act = request.getParameter("act");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>每日账单</title>
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

function trimStr(str) {
	var reg = /(^\s+)|(\s+$)/g;
	return str.replace(reg, "");
}

function onQuantityChange(idx) {
	var priceStr = trimStr($("#Price_" + idx).val());
	var quantityStr = trimStr($("#Quantity_" + idx).val());
	if (priceStr != "" && quantityStr != "") {
		var price = parseFloat(priceStr);
		var quantity = parseFloat(quantityStr);
		$("#Amount_" + idx).val(price * quantity);		
	}
	
	doSum();
}

function onAmountChange(idx) {
	var priceStr = trimStr($("#Price_" + idx).val());
	var quantityStr = trimStr($("#Quantity_" + idx).val());
	var amountStr = trimStr($("#Amount_" + idx).val());
	if (quantityStr == "" || quantityStr == "0" || quantityStr == "0.0" || quantityStr == "0.00") {
		if (amountStr != "") {
			var price = parseFloat(priceStr);
			var amount = parseFloat(amountStr);
			var quantity = Math.round(amount / price).toFixed(2) + "";
			quantity = quantity.replace(".00", "");
			quantity = quantity.replace(".0", ""); 
			$("#Quantity_" + idx).val(quantity);	
		}
	}
	
	doSum();
}

onAmountChange(<%=i%>);

function doSum() {
	var totalIncoming = 0;
	var totalOutgoing = 0;
	for (var i=0; i<10000; i++) {
		var type = $("#Type_" + i).val();
		if (type != "1" && type != "2")
			break;
		var amountStr = trimStr($("#Amount_" + i).val());
		if (amountStr != "") {
			if (type == "1") 
				totalIncoming += parseFloat(amountStr);
			else
				totalOutgoing += parseFloat(amountStr);
			if (totalIncoming <= 0) {
				alert(amountStr);
				break;
			}
		}
	}
	
	var prepareAmount = 0;
	var prepareAmountStr = $("#PrepareAmount").val();
	if (prepareAmountStr != "")
		prepareAmount = parseFloat(prepareAmountStr);
	
	var remainAmount = prepareAmount + totalIncoming - totalOutgoing;
	
	$("#SpanTotalIncoming")[0].innerHTML = totalIncoming + "";
	$("#SpanTotalOutgoing")[0].innerHTML = totalOutgoing + "";
	$("#SpanRemainAmount")[0].innerHTML = remainAmount + "";
	
	$("#TotalIncoming").val(totalIncoming);
	$("#TotalOutgoing").val(totalOutgoing);
	$("#RemainAmount").val(remainAmount);
}

function doSave() {
	if (confirm("您确定要保存么？")) {
		doSum();
		form1.submit();
	}
}

</script>
</head>
<body>
<div class="nav">当前位置：&nbsp;&nbsp;每日账单</div>
<div class="div_content">
<br/><br/>
<div align="center">
<form name="form1" id="mainForm" action="" method="post">
	<table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>日期：</strong></td>
        		<td style="text-align: left;"><input type="text" name="ItemDate" size="12" onclick="WdatePicker()" value="<%=item.ItemDate %>" /></td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>备用金：</strong></td>
        		<td style="text-align: left;"><input type="text" name="PrepareAmount" id="PrepareAmount" size="12" value="<%=Util.trimNumber(item.PrepareAmount) %>"/>&nbsp;元</td>
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
        	int lastClassId = -1;
        	for (int idx=0; idx<typesIncomings.size(); idx++) {
        		ItemType type = typesIncomings.get(idx);
        		ItemDetail d = wwService.getItemDetail(id, type.Id, ItemType.INCOMINGS);
        		%>
        	<% if (type.ClassId != lastClassId) {
        			lastClassId = type.ClassId;
        			TypeClass c = wwService.getTypeClass(lastClassId);
        		%>
        		<tr height="30px" style="background-color: #CCC;">
                	<td colspan="6">&nbsp;<strong><%=c != null ? c.Name : "" %></strong>&nbsp;</td>
                </tr>
        	<%} %>
            <tr height="30px" class="table_content_line">
                <td>&nbsp;<%=idx+1 %>&nbsp;
                    <input type="hidden" id="Type_<%=i%>" name="Type_<%=i%>" value="1"/>
                	<input type="hidden" name="TypeId_<%=i%>" value="<%=type.Id%>"/>
                	<input type="hidden" name="Id_<%=i%>" value="<%=d != null ? d.Id : 0%>"/>
                </td>
				<td>&nbsp;<%=type.Name %>&nbsp;</td>
				<td><input type="text" size="4" id="Price_<%=i %>" name="Price_<%=i %>" value="<%=Util.trimNumber(d == null ? type.Price : d.Price) %>" onblur="onQuantityChange(<%=i%>);" />&nbsp;元</td>
				<td>&nbsp;&nbsp;<input type="text" size="4" id="Quantity_<%=i %>" name="Quantity_<%=i %>" value="<%=d == null ? "" : Util.trimNumber(d.Quantity) %>" onblur="onQuantityChange(<%=i%>);" />&nbsp;<%=type.Unit %></td>
				<td><input type="text" size="4" id="Amount_<%=i %>" name="Amount_<%=i %>" value="<%=d == null ? "" : Util.trimNumber(d.Amount) %>" onblur="onAmountChange(<%=i%>);" />&nbsp;元</td>
				<td>&nbsp;<input type="text" size="50" id="Remark_<%=i %>" name="Remark_<%=i %>" value="<%=d == null ? "" : d.Remark %>" />&nbsp;</td>
			</tr>
		<%	i++;
		} %>
		<tr height="30px">
                <td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;合计&nbsp;</td>
				<td>
					<span id="SpanTotalIncoming"><%=Util.trimNumber(item.TotalIncoming) %></span>&nbsp;元
					<input type="hidden" name="TotalIncoming" id="TotalIncoming" value="<%=item.TotalIncoming %>"/>
				</td>
				<td>&nbsp;<input type="text" size="50" id="IncomingRemark" name="IncomingRemark" value="<%=item.IncomingRemark %>">&nbsp;</td>
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
        for (int idx=0; idx<typesOutgoings.size(); idx++) {
    			ItemType type = typesOutgoings.get(idx);
    			ItemDetail d = wwService.getItemDetail(id, type.Id, ItemType.OUTGOINGS);
        		%>
        	<% if (type.ClassId != lastClassId) {
        			lastClassId = type.ClassId;
        			TypeClass c = wwService.getTypeClass(lastClassId);
        		%>
        		<tr height="30px" style="background-color: #CCC;">
                	<td colspan="6">&nbsp;<strong><%=c != null ? c.Name : "" %></strong>&nbsp;</td>
                </tr>
        	<%} %>
            <tr height="30px" class="table_content_line">
                <td>&nbsp;<%=idx+1 %>&nbsp;
                    <input type="hidden" id="Type_<%=i%>" name="Type_<%=i%>" value="2"/>
                	<input type="hidden" name="TypeId_<%=i%>" value="<%=type.Id%>"/>
                	<input type="hidden" name="Id_<%=i%>" value="<%=d != null ? d.Id : 0%>"/>
                </td>
				<td>&nbsp;<%=type.Name %>&nbsp;</td>
				<td><input type="text" size="4" id="Price_<%=i %>" name="Price_<%=i %>" value="<%=Util.trimNumber(d == null ? type.Price: d.Price) %>" onblur="onQuantityChange(<%=i%>);" />&nbsp;元</td>
				<td>&nbsp;&nbsp;<input type="text" size="4" id="Quantity_<%=i %>" name="Quantity_<%=i %>" value="<%=d == null ? "" : Util.trimNumber(d.Quantity) %>" onblur="onQuantityChange(<%=i%>);" />&nbsp;<%=type.Unit %></td>
				<td><input type="text" size="4" id="Amount_<%=i %>" name="Amount_<%=i %>" value="<%=d == null ? "" : Util.trimNumber(d.Amount) %>" onblur="onAmountChange(<%=i%>);"/>&nbsp;元</td>
				<td>&nbsp;<input type="text" size="50" id="Remark_<%=i %>" name="Remark_<%=i %>" value="<%=d == null ? "" : d.Remark %>" />&nbsp;</td>
			</tr>
		<% i++;
		} %>
		<tr height="30px">
                <td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;合计&nbsp;</td>
				<td>
					<span id="SpanTotalOutgoing"><%=Util.trimNumber(item.TotalOutgoing) %></span>&nbsp;元
					<input type="hidden" name="TotalOutgoing" id="TotalOutgoing" value="<%=item.TotalOutgoing %>"/>
				</td>
				<td>&nbsp;<input type="text" size="50" id="OutgoingRemark" name="OutgoingRemark" value="<%=item.OutgoingRemark %>">&nbsp;</td>
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
        			<input type="hidden" name="RemainAmount" id="RemainAmount" />
        		</td>
        	</tr>
        </tbody>
     </table>

    <br/>
    <div style="text-align: center;">
    	<input type="hidden" name="Id" id="Id" value="<%=id %>" />
    	<input type="button" value=" 确 定 " onclick="doSave();" />&nbsp;&nbsp;&nbsp;&nbsp;
    </div>
    <br/><br/>
</form>
 </div>
</div>
</body>
</html>
