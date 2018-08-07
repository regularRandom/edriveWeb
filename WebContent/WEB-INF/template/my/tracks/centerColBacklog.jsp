<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<script src="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.js"/>"></script>
<link href="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.css"/>" rel="stylesheet" type="text/css"/>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
var strings = new Array();
strings['TEXT_YES'] = "<spring:message code='TEXT_YES'/>";
strings['TEXT_NO'] = "<spring:message code='TEXT_NO'/>";
strings['TEXT_BACKLOG_NAME'] = "<spring:message code='TEXT_BACKLOG_NAME'/>";
strings['TEXT_BACKLOG_SIZE'] = "<spring:message code='TEXT_BACKLOG_SIZE'/>";
strings['TEXT_BACKLOG_DATE'] ="<spring:message code='TEXT_BACKLOG_DATE'/>";
strings['TEXT_EMPTY_BACKLOG_LIST'] = "<spring:message code='TEXT_EMPTY_BACKLOG_LIST'/>";
strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_BACKLOG_CONFIRMATION'/>";
strings['TEXT_DELETE_BACKLOG_ERROR'] = "<spring:message code='TEXT_DELETE_BACKLOG_ERROR'/>";
strings['TEXT_EMPTY_BACKLOG_LIST'] = "<spring:message code='TEXT_EMPTY_BACKLOG_LIST'/>";
/*]]>*/
</compress:js>
</script>
<script type="text/javascript" src="<c:url value="/resources/js/functions.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/global.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/backlog.js"/>"></script>

  <div id="colTwo">
    <form id="backlogForm">
        <div id="loadingmessage"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
        <div id="backlogList" style="width: 750px;"></div>
        <div id="buttonsBacklog" style="display: none;">
            <input type="button" id="submitBacklog" value="<spring:message code="TEXT_UPLOAD_BACKLOG"/>"/>&nbsp;
            <input type="button" id="clearSelectedBacklog" value="<spring:message code="TEXT_CLEAR_SELECTED_BACKLOG"/>"/>&nbsp;
            <input type="button" id="clearBacklog" value="<spring:message code="TEXT_CLEAR_BACKLOG"/>"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </div>
    </form>
    <div id="alertBox" title="Alert"></div>
    <div id="dialogDeleteConfirm" title="Confirmation"></div>
  </div>
