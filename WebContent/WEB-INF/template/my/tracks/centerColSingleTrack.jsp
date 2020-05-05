<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=${googleApiKey}"></script>
<link href="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.css"/>" rel="stylesheet" type="text/css"/>
<script src="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.js"/>"></script>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
var trackTotalTime = ${track.totalTime};
var trackId = ${track.id};
var trackDescription = "<c:out value="${track.description}"/>";

var strings = new Array();
strings['TEXT_YES'] = "<spring:message code='TEXT_YES'/>";
strings['TEXT_NO'] = "<spring:message code='TEXT_NO'/>";
strings['TEXT_OK'] = "<spring:message code='TEXT_OK'/>";
strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'] = "<spring:message code='TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'/>";
strings['TEXT_ADD_TRACK_DESCRIPTION'] = "<spring:message code='TEXT_ADD_TRACK_DESCRIPTION'/>";
strings['TEXT_HIDE_TRACK_DESCRIPTION'] = "<spring:message code='TEXT_HIDE_TRACK_DESCRIPTION'/>";
strings['TEXT_POINT_COUNTRY'] = "<spring:message code='TEXT_POINT_COUNTRY'/>";
strings['TEXT_POINT_TIMEZONE'] = "<spring:message code='TEXT_POINT_TIMEZONE'/>";
strings['TEXT_POINT_TIME'] = "<spring:message code='TEXT_POINT_TIME'/>";
strings['TEXT_LATITUDE'] = "<spring:message code='TEXT_LATITUDE'/>";
strings['TEXT_LONGITUDE'] = "<spring:message code='TEXT_LONGITUDE'/>";
strings['TEXT_LATITUDE_DMS'] = "<spring:message code='TEXT_LATITUDE_DMS'/>";
strings['TEXT_LONGITUDE_DMS'] = "<spring:message code='TEXT_LONGITUDE_DMS'/>";
strings['TEXT_ALTITUDE'] = "<spring:message code='TEXT_ALTITUDE'/>";
strings['TEXT_SPEED'] = "<spring:message code='TEXT_SPEED'/>";
strings['UNIT_OF_SPEED'] = "${parameters.get('UNIT_OF_SPEED').getValue().getValue()}";
strings['TEXT_TRACK_START'] = "<spring:message code='TEXT_TRACK_START'/>";
strings['TEXT_DELETE_TRACK_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_TRACK_CONFIRMATION'/>";
strings['TEXT_RECALC_ERROR'] = "<spring:message code='TEXT_RECALC_ERROR'/>";
strings['TEXT_DELETE_TRACK_POINT_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_TRACK_POINT_CONFIRMATION'/>";
strings['TEXT_EMPTY_BACKLOG_LIST'] = "<spring:message code='TEXT_EMPTY_BACKLOG_LIST'/>";
strings['TEXT_TRACK_START'] = "<spring:message code='TEXT_TRACK_START'/>";
strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_BACKLOG_CONFIRMATION'/>";
strings['TEXT_DELETE_BACKLOG_ERROR'] = "<spring:message code='TEXT_DELETE_BACKLOG_ERROR'/>";
strings['TEXT_TRACK_IS_ARCHIVED'] = "<spring:message code='TEXT_TRACK_IS_ARCHIVED'/>";
</compress:js>
</script>
<script type="text/javascript" src="<c:url value="/resources/js/functions.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/global.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/functions.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/singletrack.js"/>"></script>

  <div id="colTwo">
  <div id="alertBox" title="Alert"></div>
  <div id="mapInFullScreen" title="map"></div>
  <div id="dialogDeleteConfirm"></div>
  <table width="100%" cellpadding="5">
    <tr>
        <td colspan="2">
        <p><a href="<c:url value="/my/tracks"/>" accesskey="5"><spring:message code="TEXT_MY_TRACKS"/></a> :: ${track.name} :: ${track.trackDate} &nbsp; <a href="#" class="expandMapToFullScreen"><spring:message code="TEXT_MAP_IN_FULL_SCREEN"/></a></p>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: top;" width="600px">
        <div id="map_container" style="width:600px; height:450px;">
            <div id="loadingmessage"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
        </div>
        </td>
        <td style="vertical-align: top;">
        <p><spring:message code="TEXT_TRACK_DISTANCE"/>: ${track.distance} ${parameters.get('MILEAGE_UNIT').getValue().getValue()}<br/>
           <spring:message code="TEXT_AVG_SPEED"/>: ${track.avgSpeed} ${parameters.get('UNIT_OF_SPEED').getValue().getValue()}<br/>
           <spring:message code="TEXT_START_DATE_TIME"/>: ${track.trackDate}<br/>
           <spring:message code="TEXT_END_DATE_TIME"/>: ${track.endDate}<br/>
           <spring:message code="TEXT_TOTAL_TIME"/>: <span id="totalTime"></span>
        </p>
        <p>
            <table cellpadding="2" cellspacing="2" width="100%">
            <c:choose>
                <c:when test="${track.description != null}">
                    <tr><td><p id="trackDescr">${track.description}</p></td></tr>
                </c:when>
                <c:otherwise>
                <tr>
                    <td>
                        <a href="#trackDescriptionHolder" class="trackDescriptionToggle"><spring:message code="TEXT_ADD_TRACK_DESCRIPTION"/></a>
                        <div id="trackDescriptionHolder">
                            <input type="text" size="40" name="trackDescription" id="trackDescription"/>&nbsp;
                            <input type="button" id="buttonAddDescription" value="<spring:message code="TEXT_SEND_TRACK_DESCRIPTION"/>"/>
                        </div>
                        <br/>
                    </td>
                </tr>
                </c:otherwise>
            </c:choose>
            <tiles:insertAttribute name="trackActions"/>
            </table>
        </p>
        </td>
    </tr>
    <tr>
        <td colspan="2" id="trackPointsList"><p>&nbsp;</p></td>
    </tr>
    <tr>
        <td colspan="2">
        <p><spring:message code="TEXT_STATIC_SINGLE_TRACK"/></p>
        </td>
    </tr>

  </table>
  </div>

