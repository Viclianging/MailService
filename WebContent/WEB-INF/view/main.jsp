<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${title}</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.2.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui.min.js"></script>
<link type="text/css" href="${pageContext.request.contextPath}/style/jquery-ui.min.css" rel="stylesheet">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-timepicker-zh-CN.js"></script>
<link type="text/css" href="${pageContext.request.contextPath}/style/jquery-ui-timepicker-addon.css" rel="stylesheet">
<style type="text/css">
body{text-align:center}
.normal{margin:-6% 0 0 -40%;border:1px solid #000;width:80%;height:80%;top:20%;position:absolute;left:50%}
.box{position:relative;width:80%;height:80%;top:20%; background:#ececff; cursor:move;}
.close{position:absolute; top:0px; right:0px; z-index:99;}
.sql{position:absolute;top:15px;right:0px;width:60%;height:75%;}
.tarea{position:absolute;width:95%;height:100%;right:5px}
</style>
<script type="text/javascript">
	$(function(e){
		$("#drigging").hide();
		$(".close").click(function(){
			$("#dbs option:eq(0)").attr("selected","selected");
			$("#dbtable option:gt(0)").remove();
			$("#drigging").hide();
		});
		$("#td").datetimepicker({
			   showSecond: true,
			   showMillisec: true,
			   timeFormat: 'hh:mm:ss:l'
		   });
		$("#dbs").on("change",function(){
			$(this).find("option:eq(0)").removeAttr("selected");
			var db = $(this).val();
			if(db == "sel"){
				$("#dbtable option:gt(0)").remove();
				$("#col").empty();
				$("#drigging").hide();
				return;
			}
			$.ajax({
				url:"${pageContext.request.contextPath}/mail/tables",
				type:"post",
				data:"db="+db,
				success:function(item) {
					$("#dbtable option:gt(0)").remove();
					$("#col").empty();
					$("#drigging").hide();
					var array = item.substring(1,item.length-1).split(',');
					for(var index=0; index<array.length; index++) {
						$("<option></option>").val(array[index]).text(array[index]).appendTo("#dbtable");
					}
				}
			});
		});
		$("#dbtable").on("change",function(){
			var table = $(this).val();
			$("#drigging").hide();
			if (table == "sel") {
				return;
			}
			$.ajax({
				url:"${pageContext.request.contextPath}/mail/columns",
				type:"post",
				data:"table="+table,
				success:function(item) {
					var collect = item.substring(1,item.length-1).split('][');
					var colNameArr = collect[0].split(',');
					var colTypeArr = collect[1].split(',');
					var colTable = $("#col");
					colTable.empty();
					$("#drigging").show();
					var str = "";
					for(var index=0,count=1; index<colNameArr.length; index++) {
						var colName = $.trim(colNameArr[index]);
						if (colName==null || colName=="" || colName[0]=="#") {
							continue;
						}
						str = str + "<td><b>" + colName+"</b>("+$.trim(colTypeArr[index])+")" + "</td>";
						if (count++%5==0) {
							colTable.append("<tr>"+str+"</tr>");
							str = "";
						}
					}
					if (str!="") {
						colTable.append("<tr>"+str+"</tr>");
						str=null;
					}
				}
			});
		});
		/* $("input[name='frequency'],input[name='date_frequency']").on("input",function(){
			var input=$(this).val();
			if (isNaN(Number(input))) {
				if(input.substring(input.length-1)!='+'
						&&input.substring(input.length-1)!='-'
						&&input.substring(input.length-1)!='*'
						&&input.substring(input.length-1)!='/'
						&&input.substring(input.length-1)!='('
						&&input.substring(input.length-1)!=')') {
					$(this).val(input.substring(0, input.length-1));
				}
            }
		}); */
		$("input[name='frequency'],input[name='date_frequency']").keypress(function(event) {
            var keyCode = event.which;
            if (keyCode > 39 && keyCode <58 && keyCode != 44) {
                return true;
            } else {
                return false;
            }
        }).focus(function() {
            this.style.imeMode='disabled';
        });
		$(".box").mousedown(function (e) {
			iDiffX = e.pageX - $(this).offset().left;
			iDiffY = e.pageY - $(this).offset().top;
			$(document).mousemove(function (e) {
				$(".box").css({ "left": (e.pageX - iDiffX), "top": (e.pageY - iDiffY) });
			});
		});
		$(".box").mouseup(function () {
			$(document).unbind("mousemove");
		});
		$("#submit").on("click",function(){
			$.ajax({
				url: "${pageContext.request.contextPath}/mail/",
                type: 'post',
                contentType:"application/x-www-form-urlencoded; charset=utf-8",
                data: $("#form").serialize(),
                success: function (data) {
                    if (data.length > 0) {
                    	alert(data);
                    }
                }
			});
		});
		$("#viewTask").on("click",function(){
			window.open("${pageContext.request.contextPath}/mail/view");
		});
	});
</script>
</head>
<body>
	<form action="#" method="post" id="form">
		<div class="normal">
   			<table border="0" cellspacing="20">
   				<tr>
   					<td>${labelDB}:</td>
   					<td>
						<select id="dbs" name="db" style="width:100%">
							<option value="sel">${labelSEL}</option>
							<c:forEach items="${databases}" var="item">
								<option value="${item}">${item}</option>
							</c:forEach>
						</select>
					</td>
   				</tr>
   				<tr>
   					<td>${labelTB}:</td>
   					<td>
						<select id="dbtable" name="table" style="width:100%">
							<option selected="selected" value="sel">${labelSEL}</option>
						</select>
					</td>
   				</tr>
   				<tr>
   					<td>${attachment}:</td>
   					<td><input type="text" name="attachment" style="width:100%"/></td>
   				</tr>
   				<tr>
   					<td>${mailRec}:</td>
   					<td><textarea name="receiver" style="width:100%"></textarea></td>
   				</tr>
   				<tr>
   					<td>${mailSubj}:</td>
   					<td><input name="subject" style="width:100%"/></td>
   				</tr>
   				<tr>
   					<td>${mailCon}:</td>
   					<td><textarea name="content" style="width:100%"></textarea></td>
   				</tr>
   				<tr>
   					<td colspan="2">
   						<table border="1" cellspacing="0">
   							<tr>
   								<td>
   									${labelTkst}1:
   									<table border="0" cellspacing="9" width="100%">
   										<tr>
   											<td>${labelDate}:</td>
   											<td align=left>
   												<input type="text" name="taskDate" id="td" style="width:98%"/>
   											</td>
   										</tr>
   										<tr>
   											<td>${labelFrequency}:</td>
   											<td align=left>
   												<input type="text" name="date_frequency" style="width:68%"/>
   												${suffixDF}&nbsp;&nbsp;&nbsp;
   											</td>
   										</tr>
   										<tr>
   											<td>${labelRepeat}:</td>
   											<td align=left>
   												<select name="date_repeat">
   													<option selected="selected" value="y">${valYes}</option>
   													<option value="n">${valNo}</option>
   												</select>
   											</td>
   										</tr>
   									</table>
   								</td>
   							</tr>
   						</table>
   					</td>
   				</tr>
   				<tr>
   					<td colspan="2">
   						<table border="1" cellspacing="0">
   							<tr>
   								<td>
   									${labelTkst}2:
   									<table border="0" cellspacing="9" width="100%">
   										<tr>
   											<td>${labelFrequency}:</td>
   											<td align=left>
   												<input type="text" name="frequency" style="width:68%"/>
   												${suffixf}
   											</td>
   										</tr>
   										<tr>
   											<td>${labelRepeat}:</td>
   											<td align=left>
   												<select name="repeat">
   													<option selected="selected" value="y">${valYes}</option>
   													<option value="n">${valNo}</option>
   												</select>
   											</td>
   										</tr>
   									</table>
   								</td>
   							</tr>
   						</table>
   					</td>
   				</tr>
   				<tr align=center>
   					<td colspan="2">
   						<input type="button" id="viewTask" value="${button_review}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						<input type="button" id="submit" value="${button_submit}"/>
   					</td>
   				</tr>
   			</table>
   			<div class="sql">
   				<table>
   					<tr>
   						<td><textarea name="sql" class="tarea"></textarea></td>
   					</tr>
   				</table>
   			</div>
    	</div>
    	<div class="box" id="drigging">
    		<input type="button" class="close" value="X"/>
    		<table id="col" style="width:95%;height:95%"></table>
    	</div>
	</form>
</body>
</html>