<%-- 
    Document   : index
    Created on : 02.12.2013, 05:39:13
    Author     : richter
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>JSP Page</title>
	</head>
	<body>
		<f:view>
  <h:form>
    <h:panelGrid columns="2">
      <h:outputLabel value="Celsius"></h:outputLabel>
      <h:inputText  value="#{temperatureConvertor.celsius}"></h:inputText>
    </h:panelGrid>
    <h:commandButton action="#{temperatureConvertor.celsiusToFahrenheit}" value="Calculate"></h:commandButton>
    <h:commandButton action="#{temperatureConvertor.reset}" value="Reset"></h:commandButton>
    <h:messages layout="table"></h:messages>
  </h:form>
  
  
  <h:panelGroup rendered="#{temperatureConvertor.initial!=true}">
  <h3> Result </h3>
  <h:outputLabel value="Fahrenheit "></h:outputLabel>
  <h:outputLabel value="#{temperatureConvertor.fahrenheit}"></h:outputLabel>
  </h:panelGroup>
</f:view>
	</body>
</html>
