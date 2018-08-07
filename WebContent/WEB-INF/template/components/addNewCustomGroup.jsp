<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

  <div id="dialogNewCustomGroup">
  <span class="addCustomGroupError" id="addCustomGroupErrorField"></span><br/>
  <form action="" method="post" id="addCustomGroup">
  <p>
    <div style="text-align: left;" id="newCustomGroup">
        <p style="line-height: 30px;"><spring:message code="TEXT_CUSTOM_GROUP_NAME"/>&nbsp;<input id="customGroupName" size="33"/></p>
        <p style="line-height: 30px;"><spring:message code="TEXT_CUSTOM_GROUP_DESCRIPTION"/>&nbsp;<input id="customGroupDescription" size="33"/></p>
    </div>
  </p>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>
