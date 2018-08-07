<%@ include file="/WEB-INF/views/include.jsp" %>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
    var select = $('#carId');
    var selected = "";
    $(document).ready(function() {
        $.post('<c:url value="${request.getRequestURL()}/my/cars/getMyCars"/>', function(response) {
            $.each(response, function(index, value) {
                var data="<option value=" + value.accountId + selected + ">" + value.name + "</option>";
                $(data).appendTo('#carId');
            });
        });
    });
/*]]>*/
</compress:js>
</script>

  <div id="colTwo">
  <form action="<c:url value="/my/tracks/uploadTrackFile"/>" method="POST" enctype="multipart/form-data">
  <table width="500" border="0">
    <tr>
        <td colspan="2"><p><spring:message code="TEXT_HERE_YOU_CAN_UPLOAD_NEW_TRACK"/></p></td>
    </tr>

    <c:if test="${not empty param.uploadError}">
    <tr>
        <td colspan="2" style="text-align: center;">
        <div class="uploadError">
            <p><label>${param.uploadError}</label></p>
        </div>
        </td>
    </tr>
    </c:if>

    <tr>
        <td><spring:message code="TEXT_UPLOAD_TRACK_WHICH_CAR"/></td>
        <td>
        <select id="carId" name="carId">
            <option value="0"><spring:message code="TEXT_UPLOAD_TRACK_PEDESTRIAN_TRACK"/></option>
            <option value="-1"><spring:message code="TEXT_UPLOAD_TRACK_BICYCLE_TRACK"/></option>
        </select>
        </td>
    </tr>
    <tr>
        <td width="200">
        <input type="file" style="width:180px" name="backlogFile"/>
        </td>
        <td width="300" align="left">
        <input type="submit" value="<spring:message code="TEXT_SUBMIT_FILE"/>"/>
        </td>
    </tr>
  </table>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>
