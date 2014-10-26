<%@page import="cn.wwerp.TypeClass"%>
<%@page import="java.util.Date"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	String unit = request.getParameter("Unit");
	if (unit != null) {
		ItemType item = new ItemType();
		Util.fillBean(item, request);
		item.Name = item.Name.trim();
		item.Unit = item.Unit.trim();
		
		session.setAttribute(getClass().getName(), item);
		
		if (item.Name.trim().isEmpty() || item.Unit.trim().isEmpty()) {
			response.sendRedirect("/result.jsp?Success=false"
					+ "&Text=" + Util.urlEncode("保存失败！名称/单位不能为空！"));
			return;
		}
		
		int count = wwDao.queryCount("select count(*) from ItemType where Name=? and Type=? and Id<>?", new Object[]{item.Name, item.Type, item.Id});
		if (count > 0) {
			response.sendRedirect("/result.jsp?Success=false"
					+ "&Text=" + Util.urlEncode("保存失败！【" + item.Name + "】已存在"));
			return;
		}
		
		if (item.Id > 0) {
			wwService.updateItemType(item);
		} else {
			wwService.addItemType(item);
		}
		response.sendRedirect("/addtype.jsp?Success=true"
				+ "&Text=" + Util.urlEncode("保存成功！"));
		return;
	}
	
	List<TypeClass> cs = wwService.getTypeClasses();
	
	ItemType item = null;
	int id = Util.str2int(request.getParameter("Id"));
	if (id > 0) {
		item = wwService.getItemType(id);
	} else {
		ItemType lastItem = (ItemType)session.getAttribute(getClass().getName());
		if (lastItem != null) {
			item = lastItem;
			item.Name = "";
			item.Id = 0;
		} else {
			item = new ItemType();
			item.Type = ItemType.OUTGOINGS;
			item.Price = 2;
			item.Unit = "斤";
			if (cs.size() > 0)
				item.ClassId = cs.get(0).Id;			
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>添加/修改类型</title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css">
<script src="/js/jquery-1.9.1.js"></script>
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

function doSave() {
	if (confirm("您确定要保存么？"))
		form1.submit();
}

</script>
</head>
<body>
<div class="nav">当前位置：&nbsp;&nbsp;添加修改分类</div>
<div class="div_content">
<br/><br/>
<div align="center">
<form name="form1" id="mainForm" action="" method="post">
	<table style="width: 70%;" border="0">
        <tbody>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>类型：</strong></td>
        		<td style="text-align: left;">
        			<select name="Type">
        				<option value="<%=ItemType.INCOMINGS %>" <%=item.Type == ItemType.INCOMINGS ? "selected='selected'" : "" %>>收入</option>
        				<option value="<%=ItemType.OUTGOINGS %>" <%=item.Type == ItemType.OUTGOINGS ? "selected='selected'" : "" %>>支出</option>
        			</select>
        		</td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>分类：</strong></td>
        		<td style="text-align: left;">
        			<select name="ClassId">
        				<% for (TypeClass c : cs) { %>
        				<option value="<%=c.Id %>" <%=item.ClassId == c.Id ? "selected='selected'" : "" %>><%=c.Name %></option>
        				<%} %>
        			</select>
        		</td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>名称：</strong></td>
        		<td style="text-align: left;"><input type="text" name="Name" size="12" value="<%=item.Name%>" /></td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>单价：</strong></td>
        		<td style="text-align: left;"><input type="text" name="Price" size="12" value="<%=Util.trimNumber(item.Price)%>" />&nbsp;元</td>
        	</tr>
        	<tr>
        		<td width="45%" style="text-align: right;"><strong>单位：</strong></td>
        		<td style="text-align: left;"><input type="text" name="Unit" size="12" value="<%=item.Unit%>" /></td>
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
