<%@ include file="/WEB-INF/views/include.jsp" %>

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?v=3&key=AIzaSyAKZsOo-t9RwHP3O8p5BRElyHCWVxKYk0k"></script>
<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
    var strings = new Array();
    strings['speedDivider'] = ${parameters.get("SPEED_DIVIDER").getValue().getValue()};
    strings['TEXT_OSRM_ROUTE'] = '<spring:message code="TEXT_OSRM_ROUTE"/>: ';
    strings['MILEAGE_UNIT'] = ' ${parameters.get("MILEAGE_UNIT").getValue().getValue()}<br/><ol>';
    strings['TEXT_OSRM_ROUTE_NOT_FOUND_ERROR'] = '<spring:message code="TEXT_OSRM_ROUTE_NOT_FOUND_ERROR"/>';
</compress:js>
</script>

<c:choose>
    <c:when test="${configuration.get('OSRM_DASHBOARD_ENABLED') == 1}">
<script src="<c:url value="/resources/js/route/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/js/route/functions.js"/>" type="text/javascript"></script>
    </c:when>
</c:choose>

<style>
<compress:css enabled="${compressOnOff}">
  .ui-autocomplete-loading {
    background: white url("<c:url value="/resources/images/ui-anim_basic_16x16.gif"/>") right center no-repeat;
  }
  .ui-autocomplete {
    max-height: 300px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 0px;
  }
    /* for IE 6 */
  * html .ui-autocomplete {
    height: 300px;
  }
  #locationSearch[] { width: 25em; }
</compress:css>
</style>

  <div id="colTwoCenter">
    <div id="welcome">
      <h3><spring:message code="TEXT_WELCOME_TO_SITE"/></h3>
      <p><spring:message code="TEXT_HOME_PREAMBLE"/></p>

<c:choose>
    <c:when test="${configuration.get('OSRM_DASHBOARD_ENABLED') == 1}">

      <h4><spring:message code="TEXT_HOME_PLAN_ROUTE"/></h4>
      <p style="font-size:10pt;"><spring:message code="TEXT_HOME_PLAN_ROUTE_INSTRUCTIONS_SHORT"/></p>
      <table border="0" width="100%">
      <tr>
        <td width="22%" valign="top">
        <form method="POST" id="prepareLocationSearch">
        <table width="100%" id="planningTable" border="0">
        <tr>
            <td></td>
            <td><input id="firstLocationSearch" name="locationSearch[]" class="findLocation" type="text" size="45"></td>
        </tr>
        <tr id="planningActionRow">
            <td><img src="<c:url value="/resources/images/User-Interface-Plus-icon.png"/>" border="0"/></td> 
            <td align="center"><img src="<c:url value="/resources/images/Arrow-Down-icon.png"/>" border="0"/></td>
        </tr>
        <tr id="locationSearchRow">
            <td></td>
            <td><input name="locationSearch[]" id="locationSearch[]" class="findLocation" type="text" size="45"></td>
        </tr>
        </table>
<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
        <div style="float:left;margin-left:22px;">
        <a href="<c:url value="/login"/>" id="" style="font-size: 9pt;"><spring:message code="TEXT_LOGIN_USE_CURRENT"/></a>
        </div>
</sec:authorize>
<sec:authorize access="hasAnyRole('ROLE_USER', 'ROLE_ADMIN')">
        <div style="float:left;margin-left:23px;">
        <select id="select0" name="currentPosition" style="width:200px;">
            <option value="0" selected><spring:message code="TEXT_SELECT_TO_USE_CURRENT"/></option>
            <c:forEach var="car" items="${myCars}">
                <option value="${car.id}">${car.name}</option>
            </c:forEach>
        </select>
        </div>
</sec:authorize>
        <div style="float:right;margin-right:5px;">
            <a href="#" id="clearDirectrions" style="font-size: 10pt;"><spring:message code="TEXT_CLEAR"/></a>&nbsp;
            <a href="#" id="calculateDirectrions" style="font-size: 10pt;"><spring:message code="TEXT_SEARCH"/></a>
        </div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
        <p id="result" class="routePlanningInstructions"><spring:message code="TEXT_HOME_PLAN_ROUTE_INSTRUCTIONS"/></p>
        </td>
        <td align="left" width="78%" valign="top">
        <div id="loadingmessage" style="z-index: 150; float: center; position: absolute;"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
        <div id="planningMap" style="float: center; width:100%; height:500px; z-index: 50; position: relative;"></div>
        </td>
      </tr>
      </table>

    </c:when>
</c:choose>

    </div>
  </div>
