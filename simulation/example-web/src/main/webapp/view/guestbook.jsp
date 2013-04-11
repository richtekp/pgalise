<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="de.pgalise.ejb.example.shared.*"%>
<html>
<head>
	<title>Guestbook</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<script type="text/javascript" src="/example${requestScope.getContextPath}/resources/js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="/example${requestScope.getContextPath}/resources/js/guestbook.js"></script>
	
<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    
    <link href="/example${requestScope.getContextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/example${requestScope.getContextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet">
    
    <script src="/example${requestScope.getContextPath}/resources/js/bootstrap.min.js"></script>
</head>

<body">
<h1>Guestbook</h1>
<h3>#Entries: ${guestbook.getEntries().size()}</h3>
<div id="content"></div>

<form class="form-horizontal" action="" method="POST" accept-encoding="UTF-8">
  <div class="control-group">
    <label class="control-label">Name</label>
    <div class="controls">
      <input type="text" id="name" name="name" placeholder="Name">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">Text</label>
    <div class="controls">
      <textarea id="text" name="text" rows="5" ></textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <input type="submit" class="btn btn-primary" name="submit" value="Post it"/>
    </div>
  </div>
  <script type="text/javascript">main(${guestbook})</script>
</form>
</body>
</html>