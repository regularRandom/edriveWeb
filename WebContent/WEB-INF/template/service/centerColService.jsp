<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ include file="/WEB-INF/views/datatables.jsp" %>

<link href="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.css"/>" rel="stylesheet" type="text/css"/>
<script src="<c:url value="/resources/jquery/jquery-ui-custom/jquery-ui.min.js"/>"></script>
<style>
<compress:css enabled="${compressOnOff}">
    .smallDataTableStyle { font-size: 10px; },
    .rightAlign {  text-align: right; }
</compress:css>
</style>
<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
    var strings = new Array();
    strings['TEXT_YES'] = "<spring:message code='TEXT_YES'/>";
    strings['TEXT_NO'] = "<spring:message code='TEXT_NO'/>";
    strings['TEXT_DELETE_ERROR'] = "<spring:message code='TEXT_DELETE_ERROR'/>";
    strings['TEXT_TEXT_ENTITY_TRANSLATION'] = "<spring:message code='TEXT_TEXT_ENTITY_TRANSLATION'/>";
    strings['TEXT_SAVE'] = "<spring:message code='TEXT_SAVE'/>";
    strings['TEXT_TEXT_LANGUAGE'] = "<spring:message code='TEXT_TEXT_LANGUAGE'/>";
    strings['TEXT_TEXT_ENTITY_VALUE'] = "<spring:message code='TEXT_TEXT_ENTITY_VALUE'/>";
    strings['TEXT_DELETE_TRANSLATION_CONFIRMATION'] = "<spring:message code='TEXT_DELETE_TRANSLATION_CONFIRMATION'/> ";
    strings['TEXT_UPDATE_TRANSLATION_ERROR'] = "<spring:message code='TEXT_UPDATE_TRANSLATION_ERROR'/> ";
    strings['TEXT_INSERT_TRANSLATION_ERROR'] = "<spring:message code='TEXT_INSERT_TRANSLATION_ERROR'/> ";
    strings['TEXT_FORM_TRANSLATION_ERROR'] = "<spring:message code='TEXT_FORM_TRANSLATION_ERROR'/> ";
    $(document).ready(function() {
<tiles:insertAttribute name="textConfiguration"/>
    });
/*]]>*/
</compress:js>
</script>
<script src="<c:url value="/resources/js/my/global.js"/>"></script>
<script src="<c:url value="/resources/js/service/functions.js"/>"></script>

  <div id="colTwoCenter">
  <div id="dialogDeleteConfirm"></div>
  <div id="insertUpdateTranslationDialog">
  <span class="changePasswordError" id="translationErrorField"></span><br/>
  <form action="" method="post" id="translationForm">
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_TEXT_ENTITY_KEY"/>
    <input size="35" maxlength="256" name="textEntityKey" class="textEntityKey" id="textEntityKey"/></p>
    </div>
  </p>
  <c:forEach var="language" items="${languages.getLanguages()}">
  <c:set var="lang" value="${language.getValue().getCode()}"/>
  <c:choose>
  <c:when test="${lang!='asis'}">
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_TEXT_LANGUAGE"/>:&nbsp;<c:out value="${lang}"/>
    <input size="35" maxlength="512" class="translationEntity" name="<c:out value="${lang}"/>" id="<c:out value="${lang}"/>"/></p>
    </div>
  </p>
  </c:when>
  </c:choose>
  </c:forEach>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>

  <div id="insertUpdateConfigurationDialog">
  <span class="changePasswordError" id="configurationErrorField"></span><br/>
  <form action="" method="post" id="configurationForm">
  
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_CONFIGURATION_KEY"/>
    <input size="35" maxlength="256" name="configurationKey" class="configurationKey" id="configurationKey"/></p>
    </div>
  </p>
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_CONFIGURATION_VALUE"/>
    <input size="35" maxlength="256" name="configurationValue" class="configurationValue" id="configurationValue"/></p>
    </div>
  </p>
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_CONFIGURATION_DESCRIPTION"/>
    <input size="35" maxlength="256" name="configurationDescription" class="configurationDescription" id="configurationDescription"/></p>
    </div>
  </p>
  <input type="hidden" name="configurationId" class="configurationId" id="configurationId">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>

  <table width="100%" cellpadding="2" border="0">
    <tr>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_SYSTEM_CONFIGURATION"/></h3>&nbsp;<a href="#" class="insertUpdateConfiguration" id="new"><spring:message code="TEXT_SYSTEM_ADD_CONFIGURATION"/></a>&nbsp;<a href="<c:url value="/service/reloadConfiguration"/>"><spring:message code="TEXT_SYSTEM_RELOAD_CONFIGURATION"/></a></p>
        <table width="100%" border="0">
        <tr>
            <td>
            <div id="loadingmessage2"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
            <div id="configurationList" style="width:700px;">
            </div>
            </td>
        </tr>
        </table>
        </td>
        
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_SYSTEM_TEXT2"/></h3>&nbsp;<a href="#" class="insertUpdateTranslation" id="new"><spring:message code="TEXT_SYSTEM_ADD_TRANSLATION"/></a>&nbsp;<a href="<c:url value="/service/reloadTranslations"/>"><spring:message code="TEXT_SYSTEM_RELOAD_TRANSLATIONS"/></a></p>
        <table width="100%" border="0">
        <tr>
            <td>
            <div id="loadingmessage1"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
            <div id="translationsList">
            </div>
            </td>
        </tr>
        </table>
        </td>
    </tr>
    <tr>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_SYSTEM_COUNTRIES"/></h3></p>
        <form action="<c:url value="/service/refreshCountries"/>" method="POST">
        <table width="100%" border="0">

        <c:if test="${not empty param.uploadErrorRefreshCountries}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.uploadErrorRefreshCountries}</label></p>
            </div>
            </td>
        </tr>
        </c:if>
        <tr>
            <td colspan="2">
                <spring:message code="TEXT_SYSTEM_COUNTRIES"/>&nbsp;
                <select id="countries">
                <c:forEach var="country" items="${countries}">
                <option value="${country.id}">${country.iso2} - ${country.nm}</option>
                </c:forEach>
                </select>
                &nbsp;<input type="submit" value="<spring:message code="TEXT_REFRESH_COUNTRIES"/>"/>
            </td>
        </tr>
        
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
        <p><h3><spring:message code="TEXT_SYSTEM_COUNTRIES_BOUNDARIES"/></h3></p>
        <form action="<c:url value="/service/loadBoundaries"/>" method="POST" enctype="multipart/form-data">
        <table width="100%" border="0">

        <c:if test="${not empty param.uploadErrorBoundaries}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.uploadErrorBoundaries}</label></p>
            </div>
            </td>
        </tr>
        </c:if>
        
        <tr>
            <td>
            <input type="file" style="width:180px;" name="countriesBoundariesFile"/>&nbsp;<input type="submit" value="<spring:message code="TEXT_SUBMIT_FILE"/>"/>
            </td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>

        <p><h3><spring:message code="TEXT_SYSTEM_GLOBAL_TIMEZONES"/></h3></p>
        <form action="<c:url value="/service/loadTimeZones"/>" method="POST">
        <table width="100%" border="0">

        <c:if test="${not empty param.uploadErrorBoundaries}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.uploadErrorBoundaries}</label></p>
            </div>
            </td>
        </tr>
        </c:if>

        <tr>
            <td>
            <input type="submit" value="<spring:message code="TEXT_REFRESH_TIMEZONES"/>"/>
            </td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>

        <p><h3><spring:message code="TEXT_SYSTEM_DISTRICTS"/></h3></p>
        <form action="<c:url value="/service/uploadDistricts"/>" method="POST" enctype="multipart/form-data">
        <table width="100%" border="0">
        <c:if test="${not empty param.uploadErrorDistricts}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.uploadErrorDistricts}</label></p>
            </div>
            </td>
        </tr>
        </c:if>
        <tr>
            <td>
            <input type="file" style="width:180px;" name="districtFile"/>&nbsp;<input type="submit" value="<spring:message code="TEXT_SUBMIT_FILE"/>"/>
            </td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>

        <p><h3><spring:message code="TEXT_SYSTEM_TIMEZONES"/></h3></p>
        <form action="<c:url value="/service/uploadGeoTimeZoneData"/>" method="POST" enctype="multipart/form-data">
        <table width="100%" border="0">
        <c:if test="${not empty param.uploadErrorGeoCSV}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.uploadErrorGeoCSV}</label></p>
            </div>
            </td>
        </tr>
        </c:if>
        <tr>
            <td colspan="2">
                <spring:message code="TEXT_SYSTEM_COUNTRIES_WITH_TIMEZONES"/>&nbsp;
                <select id="countriesWithTimeZones">
                <c:forEach var="country" items="${countriesWithTimeZones}">
                <option value="${country.id}">${country.iso2} - ${country.nm}</option>
                </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td><input type="file" style="width:180px;" name="geoTZFile"/>&nbsp;<input type="submit" value="<spring:message code="TEXT_SUBMIT_FILE"/>"/></td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
        </td>
        <td width="50%" style="vertical-align: top">
            <p><h3><spring:message code="TEXT_SYSTEM_TEXT4"/></h3></p>
        </td>

    </tr>
    <tr>
    </tr>
  </table>
  </div>
