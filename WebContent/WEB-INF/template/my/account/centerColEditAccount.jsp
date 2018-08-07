<%@ include file="/WEB-INF/views/include.jsp" %>

<script src="<c:url value="/resources/js/jquery.md5.js"/>"></script>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
    $(document).ready(function() {
        $("#updatePasswordDialog").hide();
        
        $(document).on('click','a.changePassword', function() {
            updatePasswordDialog();
        });

        <tiles:insertAttribute name="updatePasswordDialog"/>
    });
/*]]>*/
</compress:js>
</script>

  <div id="colTwoCenter">
  <div id="updatePasswordDialog">
  <span class="changePasswordError" id="changePasswordErrorField"></span><br/>
  <form action="" method="post" id="changePasswordForm">
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_CURRENT_PASSWORD"/>
    <input size="46" maxlength="32" value="" name="currentPassword" id="currentPassword" type="password"/></p>
    </div>
  </p>
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_NEW_PASSWORD"/>
    <input size="46" maxlength="32" value="" name="newPassword" id="newPassword" type="password" /></p>
    </div>
  </p>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>
  <table width="95%" cellpadding="5" border="0">
    <tr>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_CARS"/></h3></p>
        <table width="100%">
        <c:forEach var="myCar" items="${myCars}">
            <tr>
                <td><a href="<c:url value="/cars/${myCar.carId}"/>"><c:out value="${myCar.name}"/></a></td>
            </tr>
        </c:forEach>
        </table>
        </td>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_CUSTOM_PARAMETERS"/></h3>&nbsp;<a href="<c:url value="/my/editParameters"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        <table width="100%" border="0">
        <c:forEach var="myParameter" items="${myParameters}">
            <tr>
                <c:set var="metaData" value="${myParameter.getValue().getMetaData()}"/>
                <td align="left" width="60%"><c:out value="${metaData.toArray()[0].name}"/>:</td>
                <c:set var="parameter" value="${myParameter.getValue()}"/> <!-- get parameter from Map<String,Parameter> -->
                <td><c:out value="${parameter.getValue().getValue()}"/></td> <!-- get parameter value from Parameter -->
            </tr>
        </c:forEach>
        </table>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_ACCOUNT_DATA"/></h3>&nbsp;<a href="<c:url value="/my/edit"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        <table width="100%">
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_EMAIL"/>:</td>
                <td width="50%">${myAccountData.getEmail()}</td>
            </tr>
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_PASSWORD"/>:</td>
                <td width="50%"><a href="#" class="changePassword"><spring:message code="TEXT_CHANGE_PASSWORD"/></a></td>
            </tr>
            <tr>
            </tr>
        </table>
        </td>
        <td style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_SERVICES"/></h3>&nbsp;<a href="<c:url value="/my/editServices"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        </td>
    </tr>
  </table>
  </div>
