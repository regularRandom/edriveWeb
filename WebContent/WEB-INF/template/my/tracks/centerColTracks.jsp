<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=${googleApiKey}"></script>
<script src="<c:url value="/resources/js/moment-with-locales.js"/>"></script>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
var strings = new Array();
strings['TEXT_YES'] = "<spring:message code='TEXT_YES'/>";
strings['TEXT_NO'] = "<spring:message code='TEXT_NO'/>";
strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'] = "<spring:message code='TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'/>";
strings['TEXT_TRACK_NAME'] = "<spring:message code='TEXT_TRACK_NAME'/>";
strings['TEXT_TRACK_DATE'] = "<spring:message code='TEXT_TRACK_DATE'/>";
strings['TEXT_TRACK_DISTANCE'] = "<spring:message code='TEXT_TRACK_DISTANCE'/>";
strings['MILEAGE_UNIT'] = "${parameters.get('MILEAGE_UNIT').getValue().getValue()}";
strings['UNIT_OF_SPEED'] = "${parameters.get('UNIT_OF_SPEED').getValue().getValue()}";
strings['TEXT_AVG_SPEED'] = "<spring:message code='TEXT_AVG_SPEED'/>";
strings['TEXT_TRACK_CAR'] = "<spring:message code='TEXT_TRACK_CAR'/>";
strings['TEXT_TRACK_ACTION'] = "<spring:message code='TEXT_TRACK_ACTION'/>";
strings['TEXT_TRACK_READONLY_PUBLIC_DELETE'] = "<spring:message code='TEXT_TRACK_READONLY_PUBLIC_DELETE'/>";
strings['TEXT_EMPTY_TRACK_LIST'] = "<spring:message code='TEXT_EMPTY_TRACK_LIST'/>";
strings['TEXT_DELETE_TRACK_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_TRACK_CONFIRMATION'/>";
strings['TEXT_DELETE_ERROR'] = "<spring:message code='TEXT_DELETE_ERROR'/>";
strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_BACKLOG_CONFIRMATION'/>";
strings['TEXT_ADD_CUSTOM_GROUP_ERROR'] = "<spring:message code='TEXT_ADD_CUSTOM_GROUP_ERROR'/>";
strings['TEXT_GROUP_NAME_CANNOT_BE_EMPTY'] = "<spring:message code='TEXT_GROUP_NAME_CANNOT_BE_EMPTY'/>";
strings['TEXT_TRACK_PEDESTRIAN'] = "<spring:message code='TEXT_TRACK_PEDESTRIAN'/>";
strings['TEXT_TRACK_BICYCLE'] = "<spring:message code='TEXT_TRACK_BICYCLE'/>";
strings['TEXT_MERGE_TRACKS_CONFIRMATION'] = "<spring:message code='TEXT_MERGE_TRACKS_CONFIRMATION'/>";
strings['TEXT_ARCHIVED_TRACKS'] = "<spring:message code='TEXT_ARCHIVED_TRACKS'/>";
</compress:js>
</script>

<script type="text/javascript" src="<c:url value="/resources/js/my/global.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/functions.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/tracks.js"/>"></script>

  <div id="colTwo">
  <div id="dialogDeleteConfirm"></div>

  <tiles:insertAttribute name="addNewCustomGroup"/>

    <div>
        <p><spring:message code="TEXT_STATIC_TRACK_LIST"/></p>
    </div>
    <div id="tracksList"></div>
    <div id="buttons">
        <input type="button" id="submitMerge" value="<spring:message code="TEXT_MERGE_TRACKS"/>"/>&nbsp;
        <input type="button" id="submitArchive" value="<spring:message code="TEXT_ARCHIVE_TRACKS"/>"/>&nbsp;
        <input type="button" id="submitDelete" value="<spring:message code="TEXT_DELETE_TRACKS"/>"/>&nbsp;
        <select id="customGroupSelect"><option value=0><spring:message code='TEXT_ADD_TO_NEW_OR_EXISTING_GROUP'/></option></select>&nbsp;<input type="button" id="submitAddToCustomGroup" value="<spring:message code="TEXT_ADD_TO_CUSTOM_GROUP"/>"/>&nbsp;
    </div>
  </div>
