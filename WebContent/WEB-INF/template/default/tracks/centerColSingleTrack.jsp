<%@ include file="/WEB-INF/views/include.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false&key=AIzaSyAKZsOo-t9RwHP3O8p5BRElyHCWVxKYk0k"></script>
<script src="<c:url value="/resources/datatables/media/js/jquery.dataTables.min.js"/>"></script>
<link href="<c:url value="/resources/datatables/media/css/jquery.dataTables.css"/>" rel="stylesheet" type="text/css"/>

<script type="text/javascript">
/*<![CDATA[*/
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
    var flightPlanCoordinates = [];
    var startLatlng;
    var bounds = new google.maps.LatLngBounds();
    var oTable;
    var map;
    var floatingMarker;

    $(document).ready(function() {
        $("#dataList").hide();
        $('#totalTime').html(seconds2time(${track.totalTime}));
        $('#loadingmessage').show();        

        $(".trackDataToggle").click(function() {
            //get collapse content selector
            var collapse_content_selector = $(this).attr('href');
            //make the collapse content to be shown or hide
            var toggle_switch = $(this);
            $(collapse_content_selector).toggle(function(){
                if($(this).css('display')=='none'){
                    //change the button label to be 'Show'
                    toggle_switch.html("<spring:message code="TEXT_SHOW_TRACK_DATA"/>");
                } else {
                    //change the button label to be 'Hide'
                    oTable.fnAdjustColumnSizing();
                    toggle_switch.html("<spring:message code="TEXT_HIDE_TRACK_DATA"/>");
                }
            });
        });

        $.post('<c:url value="${request.getRequestURL()}/tracks/populateTrack/${track.id}"/>', function(response) {
            var i = 0;
            $.each(response, function(index, item) {
                flightPlanCoordinates.push(new google.maps.LatLng(item.latitude, item.longitude));
                bounds.extend(flightPlanCoordinates[i]);
                i++;
                
            });
            
            startLatlng = new google.maps.LatLng(response[0].latitude, response[0].longitude);
            $('#loadingmessage').hide();

            $('#trackPointsList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="trackPoints"></table>' );
            
            oTable = $('#trackPoints').dataTable( {
                "data": response,
                "scrollY": "400px",
                "scrollCollapse": true,
                "language": { "decimal": ",", "thousands": "." },
                "columns": [
                    { "title": "<spring:message code="TEXT_POINT_COUNTRY"/>", "class": "dt-body-center", "data" : "country", "orderable" : true },
                    { "title": "<spring:message code="TEXT_POINT_TIMEZONE"/>", "class": "dt-body-center", "data" : "timezone", "orderable" : true },
                    { "title": "<spring:message code="TEXT_POINT_TIME"/>", "class": "dt-body-center", "data" : "timestamp", "orderable" : true },
                    { "title": "<spring:message code="TEXT_LATITUDE"/>", "class": "dt-body-center", "data" : "latitude", "orderable" : false, "visible" : false  },
                    { "title": "<spring:message code="TEXT_LONGITUDE"/>", "class": "dt-body-center", "data" : "longitude", "orderable" : false, "visible" : false  },
                    { "title": "<spring:message code="TEXT_LATITUDE_DMS"/>", "class": "dt-body-center", "data" : "latitudeDMS", "orderable" : false },
                    { "title": "<spring:message code="TEXT_LONGITUDE_DMS"/>", "class": "dt-body-center", "data" : "longitudeDMS", "orderable" : false },
                    { "title": "<spring:message code="TEXT_ALTITUDE"/>", "class": "dt-body-center", "data" : "altitude", "orderable" : true },
                    { "title": "<spring:message code="TEXT_SPEED"/>, ${parameters.get('UNIT_OF_SPEED').getValue().getValue()}", "class": "dt-body-center", "data" : "speed", "orderable" : true }
                ]
            } );

            gMap = loadMap("map_container",DEFAULT_ZOOM);
            setStyle(gMap);
            floatingMarker = setMarker(gMap,startLatlng,"<spring:message code="TEXT_TRACK_START"/>");
            <tiles:insertAttribute name="clickOnTr"/>

        });
    });
</compress:js>
/*]]>*/
</script>

  <div id="colTwoCenter">
  <table width="100%" cellpadding="5">
    <tr>
        <td colspan="2">
        <p><a href="<c:url value="/tracks"/>" accesskey="5"><spring:message code="TEXT_TRACKS"/></a> :: ${track.name} :: ${track.trackDate}</p>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: top;" width="600px">
        <div id="map_container" style="width:600px; height:450px;">
            <div id="loadingmessage"><img class="loading" src="<c:url value="/resources/images/loading.gif"/>" border="0"/></div>
        </div>
        </td>
        <td style="vertical-align: top;">
        <p><spring:message code="TEXT_TRACK_DISTANCE"/>: ${track.distance} ${parameters.get('MILEAGE_UNIT').getValue().getValue()}<br/>
           <spring:message code="TEXT_AVG_SPEED"/>: ${track.avgSpeed} ${parameters.get('UNIT_OF_SPEED').getValue().getValue()}<br/>
           <spring:message code="TEXT_START_DATE_TIME"/>: ${track.trackDate}<br/>
           <spring:message code="TEXT_END_DATE_TIME"/>: ${track.endDate}<br/>
           <spring:message code="TEXT_TOTAL_TIME"/>: <span id="totalTime"></span>
        </p>
        <p>
            <table cellpadding="2" cellspacing="2" width="100%">
            <c:choose>
                <c:when test="${track.description != null}">
                    <tr><td><p id="trackDescr">${track.description}</p></td></tr>
                </c:when>
            </c:choose>
            <c:if test="${ sessionScope.account != null && track.ownerId == sessionScope.account.getId() }">
                <tiles:insertAttribute name="trackActions"/>
            </c:if>
            </table>
        </p>
        <a href="#dataList" class="trackDataToggle"><spring:message code="TEXT_SHOW_TRACK_DATA"/></a>
        </td>
    </tr>
    <tr id="dataList">
        <td colspan="2"><span id="trackPointsList"></span></td>
    </tr>
  </table>
  </div>
