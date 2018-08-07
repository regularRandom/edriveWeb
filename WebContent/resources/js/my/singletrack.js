var bounds = new google.maps.LatLngBounds();

$(document).ready(function() {
    $('#totalTime').html(seconds2time(trackTotalTime));

    $('#loadingmessage').show();
    $('#trackActionsDescriptionHolder').hide();

    showHideHolders(".showTrackActionsHelp",strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'],strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION']);
    showHideHolders(".trackDescriptionToggle",strings['TEXT_ADD_TRACK_DESCRIPTION'],strings['TEXT_HIDE_TRACK_DESCRIPTION']);

    if( trackDescription.length === 0 ) {
        $('#trackDescriptionHolder').hide();
    }

    $.post('@webContext@/my/tracks/populateTrack/' + trackId, function(response) {
        var i = 0;
        if( $.trim(response) == '' ) {
            $('#loadingmessage').html(strings['TEXT_TRACK_IS_ARCHIVED']);
            return false;
        } else {
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
                "bStateSave" : true,
                "aoColumnDefs" : [
                    {
                        aTargets: [9],    // Column number which needs to be modified
                        mRender: function (data,type,full) {   // row, data contains the object and value for the column
                            return '<input type="checkbox" class="trackPointAction" id="' + full.id + '"/><a href="#" class="deleteTrackPoint" id="' + full.id + '"><img src="@webContext@/resources/images/button-delete.png" border="0"/></a>';
                        }
                    }
                ],
                "columns": [
                    { "title": strings['TEXT_POINT_COUNTRY'], "class": "dt-body-center", "data" : "country", "orderable" : true },
                    { "title": strings['TEXT_POINT_TIMEZONE'], "class": "dt-body-center", "data" : "timezone", "orderable" : true },
                    { "title": strings['TEXT_POINT_TIME'], "class": "dt-body-center", "data" : "timestamp", "orderable" : true },
                    { "title": strings['TEXT_LATITUDE'], "class": "dt-body-center", "data" : "latitude", "orderable" : false, "visible" : false },
                    { "title": strings['TEXT_LONGITUDE'], "class": "dt-body-center", "data" : "longitude", "orderable" : false, "visible" : false },
                    { "title": strings['TEXT_LATITUDE_DMS'], "class": "dt-body-center", "data" : "latitudeDMS", "orderable" : false },
                    { "title": strings['TEXT_LONGITUDE_DMS'], "class": "dt-body-center", "data" : "longitudeDMS", "orderable" : false },
                    { "title": strings['TEXT_ALTITUDE'], "class": "dt-body-center", "data" : "altitude", "orderable" : true },
                    { "title": strings['TEXT_SPEED'] + ", " + strings['UNIT_OF_SPEED'], "class": "dt-body-center", "data" : "speed", "orderable" : true },
                    { "title": "<input type='checkbox' class='trackPointActions'/><a href='#' class='deleteTrackPoints'><img src='@webContext@/resources/images/button-delete.png' border='0'/></a>", "class": "dt-body-center", "data" : "id", "orderable" : false }
                ]
            } );

            gMap = loadMap("map_container",DEFAULT_ZOOM);
            setStyle(gMap);
            floatingMarker = setMarker(gMap,startLatlng,strings['TEXT_TRACK_START']);
        }

        $('#trackPoints tbody').on( 'click', 'tr', function () {
            if ( $(this).hasClass('selected') ) {
                $(this).removeClass('selected');
            } else {
                oTable.$('tr.selected').removeClass('selected');
                $(this).addClass('selected');
                var myLatlng = new google.maps.LatLng(oTable.fnGetData(this).latitude,oTable.fnGetData(this).longitude);
                floatingMarker.setMap(null);
                floatingMarker = setMarker( gMap, myLatlng, strings['TEXT_LATITUDE'] + ": " + oTable.fnGetData(this).latitude + ", " + strings['TEXT_LONGITUDE'] + ": " + oTable.fnGetData(this).longitude );
                gMap.setCenter(myLatlng);
            }
        });

        if( trackDescription.length === 0 ) {
            $('#buttonAddDescription').on('click', function() {
                if( $('#trackDescription').val() != "" ) {
                    updateTrackDescription( trackId, $('#trackDescription').val() );
                    window.location.replace("@webContext@/my/tracks/show/" + trackId);
                }
            });
        }
    });

    $(document).on('change', 'input.trackAction', function() {
        updateTrack( $(this).attr('id'), $(this).attr('name'), $(this).is(':checked') );
    });

    $(document).on('click','a.deleteTrack', function() {
        var data = new Array();
        data.push({
            id : $(this).attr('id')
        });
        fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_TRACK_CONFIRMATION'] + " ","@webContext@/my/tracks/deleteTracks","@webContext@/my/tracks");
    });

    $(document).on('click','a.recalcTrack', function() {
        $.post( "@webContext@/my/tracks/recalcTrack/", { trackId : $(this).attr('id') }, function(data) {
                if( $.trim(data) == '' ) {
                    alert(strings['TEXT_RECALC_ERROR']);
                } else {
                    location.reload();
                }
            },
            'json' // I expect a JSON response
        );
        return false;
    });

    $(document).on('click','a.expandMapToFullScreen', function() {
        fnMapInFullScreen();
    });

    $(document).on('click','a.deleteTrackPoint', function() {
        var data = new Array();
        data.push({
            id : $(this).attr('id')
        });
        fnOpenDeleteConfirmDialog(data, strings['TEXT_DELETE_TRACK_POINT_CONFIRMATION'] + " ","@webContext@/my/tracks/deleteTrackPoints","@webContext@/my/tracks/show/" + trackId);
    });

    $(document).on('click','a.deleteTrackPoints', function() {
        var data =  $("input.trackPointAction:checked").map(function(){
            return { id : this.id };
        }).get();
        if( data.length == 0 ) {
            fnOpenAlert(strings['TEXT_EMPTY_BACKLOG_LIST']);
        } else {
            fnOpenDeleteConfirmDialog(data, strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] + " ","@webContext@/my/tracks/deleteTrackPoints","@webContext@/my/tracks/show/" + trackId);
        }
        return false;
    });

    $(document).on('change','input.trackPointActions', function() {
        changeTrackPointActions();
    });

    $(document).on('change','input.trackPointAction', function() {
        changeTrackPointAction();
    });
});

