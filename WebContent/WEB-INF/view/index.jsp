<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${title}</title>
<style type="text/css">
body{ text-align:center}
#div{margin:-80px 0 0 -350px;border:1px solid #000;width:700px;height:350px;top:20%;position:absolute;left:50%} 
</style>
<script type="text/javascript">
	var prompt="${loginFailed}"
	if(prompt != "") {
		alert(prompt);
	}
</script>
</head>
<body>
    <form action="${pageContext.request.contextPath}/login/" method="post">
    	<div id="div">
   			<table height="150px" border="0" align=center>
   				<tr>
   					<td>${label_username}:</td>
   					<td><input type="text" name="username"/></td>
   				</tr>
   				<tr>
   					<td>${label_password}:</td>
   					<td><input type="password" name="password"/></td>
   				</tr>
   				<tr align=center>
   					<td colspan="2"><input type="submit" value="${label_login}"></td>
   				</tr>
   			</table>
    	</div>
    </form>
</body>
</html>