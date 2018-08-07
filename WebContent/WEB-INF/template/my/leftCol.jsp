<%@ include file="/WEB-INF/views/include.jsp" %>

<div id="colOne">
<table>
    <tr>
        <td align="center">
        <h3><a href="<c:url value="/my/tracks"/>"><spring:message code="TEXT_MY_TRACKS"/></a></h3>
        </td>
    </tr>
    <tr>
        <td>
        <div class="boxed">
      <ul>
        <li class="first"><a href="<c:url value="/my/tracks/uploadTrack"/>"><spring:message code="TEXT_UPLOAD_NEW_TRACK"/></a></li>
        <li><a href="<c:url value="/my/tracks/checkBacklog"/>"><spring:message code="TEXT_CHECK_BACKLOG"/></a></li>
        <li><a href="<c:url value="/my/groups"/>"><spring:message code="TEXT_GROUP_MANAGER"/></a></li>
        <li><a href="<c:url value="/my/tracks/planVoyage"/>"><spring:message code="TEXT_PLAN_NEW_TRACK"/></a></li>
        <li><a href="<c:url value="/my/recyclebin"/>"><spring:message code="TEXT_RECYCLEBIN"/></a></li>
      </ul>
        </div>
        </td>
    </tr>
</table>
</div>
