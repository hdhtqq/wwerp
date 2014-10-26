<%@page import="cn.wwerp.TypeClass"%>
<%@page import="cn.wwerp.StatItem"%>
<%@page import="cn.wwerp.Item"%>
<%@page import="cn.wwerp.ItemDetail"%>
<%@page import="cn.wwerp.ItemType"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html; charset=utf-8" language="java"%>
<%@ include file="/common.jsp"%>
<%
	if ("del".equals(request.getParameter("act"))) {
		 int id = Util.str2int(request.getParameter("Id"));
		 int count = wwDao.queryCount("select * from ItemDetail where TypeId=?", new Object[]{id});
		 if (count > 0) {
			 response.sendRedirect("/result.jsp?Success=false"
						+ "&Text=" + Util.urlEncode("删除失败！分类已使用，不能删除！"));
				return;			 
		 } else {
			 wwService.deleteItemType(id);	 
		 }
	}
	List<ItemType> list = wwService.getTypes();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>分类管理</title>
<link rel="stylesheet" href="/css/ww.css" />
<link rel="stylesheet" href="/js/jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css">
<script src="/js/jquery-1.9.1.js"></script>
<script src="/js/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"></script>
<script src="/js/yx.js"></script>
<script>
$(function() {
	YX.globalInit();
});

function addtype(){	
	document.location.href="addtype.jsp";
}
</script>
</head>
<body>
<div class="nav">当前位置：&nbsp;&nbsp;分类管理</div>
<div class="div_content">
<br/><br/>
<div align="center">
<form name="form1" id="mainForm" action="" method="post">
	<table border="0" style="width:90%;">
        <tbody>
        	<tr>
        		<td style="text-align: right;"><input type="button" value="添加分类"  onclick="javascript:addtype();" /></td>
        	</tr>
        </tbody>
     </table>
     <br/>
    <table class="table_content" style="width:90%;">
        <tbody>
        	<tr>
        		<th width="10%">序号</th>
        		<th width="15%">类型</th>
        		<th width="15%">分类</th>
        		<th width="15%">名称</th>
        		<th width="15%">单价</th>
        		<th width="15%">单位</th>
        		<th >操作</th>
        	</tr>
        <%
        	int i = 0;
        	for (ItemType item : list) {
        		TypeClass c = wwService.getTypeClass(item.ClassId);
        		%>
            <tr height="30px"  class="table_content_line">
                <td>&nbsp;<%=++i %>&nbsp;</td>
                <td>&nbsp;<%=item.Type == ItemType.INCOMINGS ? "收入" : "支出"%>&nbsp;</td>
                <td>&nbsp;<%=c != null ? c.Name : ""%>&nbsp;</td>
				<td>&nbsp;<%=item.Name%>&nbsp;</td>
				<td>&nbsp;<%=Util.trimNumber(item.Price)%>&nbsp;元</td>
				<td>&nbsp;<%=item.Unit%>&nbsp;</td>
				<td>&nbsp;
				<a href="addtype.jsp?Id=<%=item.Id%>">【修改】</a>&nbsp;
				<a href="types.jsp?Id=<%=item.Id%>&act=del">【删除】</a>&nbsp;</td>
			</tr>
		<%} %>
        </tbody>
    </table>
</form>
<br/><br/>
 </div>
</div>
</body>
</html>
