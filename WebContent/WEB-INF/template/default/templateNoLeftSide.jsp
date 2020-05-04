<%@ include file="/WEB-INF/views/include.jsp" %>
<compress:html removeIntertagSpaces="true">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<!-- EDRV-251: clickjack -->
<style id="antiClickjack">
    body{display:none !important;}
</style>
<script type="text/javascript">
    if (self === top) {
        var antiClickjack = document.getElementById("antiClickjack");
        antiClickjack.parentNode.removeChild(antiClickjack);
    } else {
        top.location = self.location;
    }
</script>

<title><spring:message code="TEXT_EDRIVE"/></title>
<sec:csrfMetaTags />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<script src="<c:url value="/resources/jquery/jquery-latest.min.js"/>"></script>
<script src="https://code.jquery.com/jquery-migrate-3.2.0.js"></script>
<link href="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.css"/>" rel="stylesheet" type="text/css"/>
<script src="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.js"/>"></script>

<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
<script src="<c:url value="/resources/js/functions.js" />" type="text/javascript"></script>

</head>
<body>
<div id="wrapper">
    <tiles:insertAttribute name="header"/>
    <div id="content">
        <tiles:insertAttribute name="centerCol"/>
    </div>
    <!-- <tiles:insertAttribute name="footer"/> -->
</div>
</body>
</html>
</compress:html>