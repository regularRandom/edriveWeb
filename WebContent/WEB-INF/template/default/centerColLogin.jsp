<%@ include file="/WEB-INF/views/include.jsp" %>

  <div id="colTwoCenter">
  <div id="login-box">
 
        <c:if test="${not empty msg}">
            <div class="msg">${msg}</div>
        </c:if>

<form name="j_loginForm" action="<c:url value='login' />" method="POST" class="bootstrap-frm">
        <c:if test="${not empty error}">
        <div class="loginError">
            <label>${error}</label>
        </div>
        </c:if>
    <label>
        <span><spring:message code="TEXT_USERNAME"/></span>
        <input type="text" name="username" value='' placeholder="Your username" style="width:220px;"/>
    </label>
    
    <label>
        <span><spring:message code="TEXT_PASSWORD"/></span>
        <input id="j_password" type="password" name="password" placeholder="Your password" style="width:220px;"/>
    </label>
    <label>
        <span style="width: 350px; display: block;">
        <spring:message code="TEXT_REMEMBER_ME"/>&nbsp;
        <input type="checkbox" id="_spring_security_remember_me" name="_spring_security_remember_me"/>&nbsp;
        <input id="j_loginSubmit" name="submit" type="submit" value="<spring:message code="TEXT_SUBMIT"/>"/>
    </label>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>
    </div>
  </div>
