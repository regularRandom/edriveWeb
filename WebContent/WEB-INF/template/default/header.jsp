<%@ include file="/WEB-INF/views/include.jsp" %>

<div id="header">
    <div id="menu">
    <ul>
      <li><a href="<c:url value="/home"/>" accesskey="1"><img src="<c:url value="/resources/images/home.png"/>" border="0" alt="<spring:message code="TEXT_HOME"/>"/></a></li>
<sec:authorize access="hasRole('ROLE_ADMIN')">
      <li><a href="<c:url value="/service"/>" accesskey="4"><spring:message code="TEXT_SYSTEM_DATA"/></a></li>
</sec:authorize>
      <li><a href="<c:url value="/my/account"/>" accesskey="2"><spring:message code="TEXT_GARAGE"/></a></li>
<sec:authorize access="hasAnyRole('ROLE_USER', 'ROLE_ADMIN')">
      <li><a href="<c:url value="/notes"/>" accesskey="4"><spring:message code="TEXT_NOTES"/></a></li>
</sec:authorize>
      <li><a href="<c:url value="/tracks"/>" accesskey="5"><spring:message code="TEXT_TRACKS"/></a></li>
      <li><a href="<c:url value="/home/about"/>" accesskey="7"><spring:message code="TEXT_ABOUT"/></a></li>
    </ul>
  </div>
<sec:authorize access="hasAnyRole('ROLE_USER', 'ROLE_ADMIN')">
    <form action="<%=request.getContextPath()%>/logout" method="POST" id="headerForm">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="image" src="<c:url value="/resources/images/logout.png"/>" border="0" alt="<spring:message code="TEXT_LOGOUT"/>"/>
    </form>
</sec:authorize>

<!--  <div id="search">
    <form id="form1" method="get" action="#">
      <div>
        <input type="text" name="textfield" id="textfield" />
        <input type="submit" name="submit" id="submit" value="Search" />
      </div>
    </form>
  </div> -->
  <div id="lang">
    <ul>
        <li><a href="?lang=en">EN</a>&nbsp;</li>
        <li><a href="?lang=ru">RU</a>&nbsp;</li>
    </ul>
  </div>
</div>
