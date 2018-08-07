<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<link href="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.css"/>" rel="stylesheet" type="text/css"/>
<script src="<c:url value="/resources/js/moment-with-locales.js"/>"></script>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
var strings = new Array();
strings['TEXT_TRACK_NAME'] = "<spring:message code='TEXT_TRACK_NAME'/>";
strings['TEXT_TRACK_DATE'] = "<spring:message code='TEXT_TRACK_DATE'/>";
strings['TEXT_TRACK_DISTANCE'] = "<spring:message code='TEXT_TRACK_DISTANCE'/>";
strings['TEXT_AVG_SPEED'] = "<spring:message code='TEXT_AVG_SPEED'/>";
strings['MILEAGE_UNIT'] = "${parameters.get('MILEAGE_UNIT').getValue().getValue()}";
strings['UNIT_OF_SPEED'] = "${parameters.get('UNIT_OF_SPEED').getValue().getValue()}";
/*]]>*/
</compress:js>
</script>

<script type="text/javascript" src="<c:url value="/resources/js/default/tracks.js"/>"></script>

  <div id="colTwoCenter">
    <div class="loadingmessage"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
    <div id="loadMyTrack" style="float: right"><p><a href="<c:url value="/my/tracks"/>"><spring:message code="TEXT_MY_TRACKS"/></a>&nbsp;&nbsp;&nbsp;<a href="<c:url value="/my/tracks/uploadTrack"/>"><spring:message code="TEXT_UPLOAD_NEW_TRACK"/></a></p></div>
    <div id="tracksList"></div>

  </div>
