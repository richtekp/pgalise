<%-- 
    Document   : index
    Created on : 15.12.2013, 09:47:06
    Author     : richter
--%>

<%@page import="javax.ejb.EJB"%>
<%@page import="de.pgalise.simulation.service.IdGenerator"%>
<%
@EJB
IdGenerator idGenerator;
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>JSP Page</title>
	</head>
	<body>
		<h1>Hello World!</h1>
	</body>
</html>
