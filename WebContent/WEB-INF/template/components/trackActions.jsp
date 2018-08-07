<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if test="${track.readOnly}">
<c:set var="readOnly" scope="request" value="checked"/>
</c:if>
<c:if test="${track.publicTrack}">
<c:set var="publicTrack" scope="request" value="checked"/>
</c:if>
            <tr>
                <td>
                    <table width="100%">
                    <tr>
                        <td width="50%"><input type="checkbox" name="readOnly" id="${track.id}" class="trackAction" <c:out value="${readOnly}"/>>&nbsp;<spring:message code="TEXT_TRACK_READONLY"/></td>
                        <td width="50%"><input type="checkbox" name="publicTrack" id="${track.id}" class="trackAction" <c:out value="${publicTrack}"/>>&nbsp;<spring:message code="TEXT_TRACK_PUBLIC"/></td>
                    </tr>
                    <tr>
                        <td width="50%"><a href="#" class="recalcTrack" id="${track.id}">&nbsp;<img src="<c:url value="/resources/images/button-recalc.png"/>" border="0"/>&nbsp;<spring:message code="TEXT_RECALC_TRACK"/></a></td>
                        <td width="50%"><a href="<c:url value="/my/tracks/exportGpx/${track.id}"/>" class="exportGpx" id="${track.id}">&nbsp;<img src="<c:url value="/resources/images/button-export.png"/>" border="0"/>&nbsp;<spring:message code="TEXT_EXPORT_GPX1"/></a></td>
                    </tr>
                    <tr>
                        <td width="50%"><a href="#" class="deleteTrack" id="${track.id}">&nbsp;<img src="<c:url value="/resources/images/button-delete.png"/>" border="0"/>&nbsp;<spring:message code="TEXT_DELETE_TRACK"/></a></td>
                        <td width="50%"><a href="<c:url value="/my/tracks/exportNmea/${track.id}"/>" class="exportGpx" id="${track.id}">&nbsp;<img src="<c:url value="/resources/images/button-export.png"/>" border="0"/>&nbsp;<spring:message code="TEXT_EXPORT_BLF"/></a></td>
                    </tr>
                    </table>
                </td>
            </tr>
