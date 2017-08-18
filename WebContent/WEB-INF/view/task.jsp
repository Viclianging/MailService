<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${title}</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.2.1.js"></script>
<style type="text/css">
.tbl{margin:-6% 0 0 -40%;border:1px solid #000;width:80%;height:80%;top:20%;position:absolute;left:50%}
</style>
<script type="text/javascript">
	$(function(){
		$.ajax({
			url:"${pageContext.request.contextPath}/mail/viewTask",
			type:"post",
			contentType: "application/x-www-form-urlencoded; charset=utf-8",
			success:function(data){
				var tb = $("#tb");
				tb.append("<tr style='background-color:lightgray'>"+"<td>"+"任务编号"+"</td>"+"<td>"+"设定日期"+"</td>"+"<td>"+"设定频率"+"</td>"+"<td>"+"是否设定重复"+"</td>"+"<td>"+"操作"+"</td>"+"</tr>");
				if (data == "null") {
					return;
				}
				var json = eval("("+data+")");
				for (var i = 0; i < json.length; i++) {
					var obj = json[i];
					var frequency = obj.date_frequency == '-' ? obj.frequency : obj.date_frequency;
					var repeat = obj.date_repeat == '-' ? obj.repeat : obj.date_repeat;
					repeat = repeat == 'y' ? "是" : "否";
					tb.append("<tr title='"+obj.task_title+"'>"
							+"<td>任务"+(i+1)+":</td>"
							+"<td>"+obj.task_date+"</td>"
							+"<td>"+frequency+"</td>"
							+"<td align='center'>"+repeat+"</td>"
							+"<td>"+"<input type='button' value='remove' name='"+obj.task_no+"'>"+"</td>"
							+"</tr>");
				}
			}
		});
		$(".tbl").on("click","input[value='remove']",function(){
			if (!confirm("确定删除？")) {
				return;
			}
			var val = $(this).attr("name");
			var item = $(this).parent().parent();
			$.ajax({
				url:"${pageContext.request.contextPath}/mail/remove",
				type:"get",
				data:"taskNo="+val,
				success:function(data) {
					if (data == "N") {
						alert("删除失败！");
					} else {
						item.remove();
					}
				}
			});
		});
	});
</script>
</head>
<body>
	<div class="tbl">
		<table border="0" id="tb" cellspacing="10"></table>
	</div>
</body>
</html>