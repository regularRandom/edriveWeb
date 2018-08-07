<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
    var strings = new Array();
    strings['TEXT_ACCOUNT_NEW_CUSTOM_GROUP'] = "<spring:message code='TEXT_ACCOUNT_NEW_CUSTOM_GROUP'/>";
    strings['TEXT_CLEAR_SELECTED_GROUPS'] = "<spring:message code='TEXT_CLEAR_SELECTED_GROUPS'/>";
    strings['TEXT_CLEAR_GROUPS'] = "<spring:message code='TEXT_CLEAR_GROUPS'/>";
    strings['TEXT_GROUP_NAME'] = "<spring:message code='TEXT_GROUP_NAME'/>";
    strings['TEXT_OBJECTS_COUNT'] = "<spring:message code='TEXT_OBJECTS_COUNT'/>";
    strings['TEXT_GROUP_ACTION'] = "<spring:message code='TEXT_GROUP_ACTION'/>";
    strings['TEXT_DELETE_GROUP_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_GROUP_CONFIRMATION'/>";
    strings['TEXT_YES'] = "<spring:message code='TEXT_YES'/>";
    strings['TEXT_NO'] = "<spring:message code='TEXT_NO'/>";
    strings['TEXT_EMPTY_GROUPS_LIST'] = "<spring:message code='TEXT_EMPTY_GROUPS_LIST'/>";
    strings['TEXT_ADD_CUSTOM_GROUP_ERROR'] = "<spring:message code='TEXT_ADD_CUSTOM_GROUP_ERROR'/>";
    strings['TEXT_GROUP_NAME_CANNOT_BE_EMPTY'] = "<spring:message code='TEXT_GROUP_NAME_CANNOT_BE_EMPTY'/>";
</compress:js>
</script>

<script type="text/javascript" src="<c:url value="/resources/js/my/global.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/functions.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my/groups.js"/>"></script>


  <div id="colTwo">

  <tiles:insertAttribute name="addNewCustomGroup"/>

    <form id="groupsForm">
        <div id="loadingmessage"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
        
        <div id="groupsList" style="width: 700px;"></div>
        <div id="buttonsGroups">
            <input type="button" id="addNewCustomGroup" value="<spring:message code="TEXT_ADD_NEW_CUSTOM_GROUP"/>"/>&nbsp;
            <input type="button" id="clearSelectedGroups" value="<spring:message code="TEXT_CLEAR_SELECTED_GROUPS"/>"/>&nbsp;
            <input type="button" id="deleteGroups" value="<spring:message code="TEXT_DELETE_GROUPS"/>"/>
        </div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    </form>
    <div id="alertBox" title="Alert"></div>
    <div id="dialogDeleteConfirm" title="Confirmation"></div>
  </div>
