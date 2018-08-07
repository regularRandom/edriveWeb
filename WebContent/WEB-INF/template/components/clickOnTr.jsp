<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

            $('#trackPoints tbody').on( 'click', 'tr', function () {
                if ( $(this).hasClass('selected') ) {
                    $(this).removeClass('selected');
                } else {
                    oTable.$('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    var myLatlng = new google.maps.LatLng(oTable.fnGetData(this).latitude,oTable.fnGetData(this).longitude);
                    floatingMarker.setMap(null);
                    floatingMarker = setMarker( gMap, myLatlng, "<spring:message code="TEXT_LATITUDE"/>: " + oTable.fnGetData(this).latitude + ", <spring:message code="TEXT_LONGITUDE"/>: " + oTable.fnGetData(this).longitude );
                    gMap.setCenter(myLatlng);
                }
            });
