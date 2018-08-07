<%@ include file="/WEB-INF/views/include.jsp" %>
<compress:html removeIntertagSpaces="true">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
</head>
<body>
<div id="wrapper">
    <tiles:insertAttribute name="header"/>
    <div id="content">
        <tiles:insertAttribute name="leftCol"/>
        <tiles:insertAttribute name="centerCol"/>
    </div>
</div>
</body>
</html>
</compress:html>