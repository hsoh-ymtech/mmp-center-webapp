<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
</head>
<body>
<form action="/authenticate" method="post">
	<h1>로그인</h1>
	<input type="text" name="id" />
	<input type="password" name="pwd" />
	<input type="submit" value="로그인" />			
</form>
</body>
</html>