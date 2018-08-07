var DEFAULT_ZOOM = 6;
var CUSTOM_ZOOM = 6;

$(document).ready(function(){
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.status + ": " + thrownError);
        }
    });
});

function getUrlParameter() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

/**
 * Converts time from total seconds
 * to HH MI SS format
 */
seconds2time = function (t) {
    return (new Date(t%86400*1000)).toUTCString().replace(/.*(\d{2}):(\d{2}):(\d{2}).*/, "$1h $2m $3s");
}

function changeTrackPointActions() {
    if( $('input.trackPointAction').is(':checked') )
        $('input.trackPointAction').prop('checked', false);
    else
        $('input.trackPointAction').prop('checked', true);
}

function changeTrackPointAction() {
    if( $("input.trackPointAction:checked").length > 0 )
        $("input.trackPointActions").prop("checked", true);
    else {
        $("input.trackPointActions").prop("checked", false);
    }
}

function round(value, decimals) {
    return Number(Math.round(value+'e'+decimals)+'e-'+decimals);
}

function getYearsToCurrent( startYear, endYear ) {
    var currentYear = (endYear == undefined) ? new Date().getFullYear() : endYear, years = [];
    startYear = startYear || 1900;

    while ( startYear <= currentYear ) {
        years.push(currentYear--);
    }
    return years;
}

Array.prototype.getItemByParam = function(paramPair) {
   var key = Object.keys(paramPair)[0];
   return this.find(function(item){return ((item[key] == paramPair[key]) ? true: false)});
}

// google map

    function setStyle( map ) {
        var style = [{
            featureType: "administrative",
            elementType: "geometry",
            stylers: [
                    { visibility: "off" }
                ]
        },
        {
            featureType: "administrative.country",
            elementType: "geometry.stroke",
            stylers: [{
                visibility: "on"
            }]
        }];
        var styledMap = new google.maps.StyledMapType(style);
        map.mapTypes.set('cleanMap', styledMap);
        map.setMapTypeId('cleanMap');
    }

    function loadMap( container, pZoom ) {
        var mapOptions = {
          zoom: pZoom,
          center: startLatlng,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        var lMap = new google.maps.Map(document.getElementById(container),mapOptions);

        lMap.fitBounds(bounds);
        var listener = google.maps.event.addListener(lMap, "idle", function() { 
            if (lMap.getZoom() > 16) lMap.setZoom(10); 
            google.maps.event.removeListener(listener); 
        });

        if( flightPlanCoordinates.length > 0 ) {
            flightPath = new google.maps.Polyline({
                path: flightPlanCoordinates,
                geodesic: true,
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 2
            });
            flightPath.setMap(lMap);
        }

        return lMap;
    }

    function setMarker( pMap, pStartLatlng, pTitle ) {
        var marker = new google.maps.Marker({
          position: pStartLatlng,
          map: pMap,
          title: pTitle
        });
        return marker;
    }

    function fnOpenAlert(text) {
        $("#alertBox").html(text);
        $("#alertBox").dialog({
            resizable: false,
            modal: true,
            title: "Alert",
            height: 200,
            width: 400,
            buttons: [{
                text : 'OK',
                click : function () {
                    $(this).dialog('close');
                }
            }]
        });
    }

// id - what delete
// text - text message in dialog
// url - url to send request
// loc - redirect location
function fnOpenDeleteConfirmDialog(data,text,url,loc) {
    if( data.length == 1 && data[0].id != null ) {
        $("#dialogDeleteConfirm").html(text + data[0].id + "?");
    } else {
        $("#dialogDeleteConfirm").html(text);
    }

    var buttonYes = strings['TEXT_YES'];
    var buttonNo = strings['TEXT_NO'];
    $("#dialogDeleteConfirm").dialog({
        resizable: false,
        modal: true,
        title: "Confirmation",
        height: 200,
        width: 400,
        buttons: [{
                text : buttonYes,
                click : function () {
                            $(this).dialog('close');
                            deleteConfirmDialogCallback(data,url,loc);
                        }
            },
            {
                text : strings['TEXT_NO'],
                click : function () {
                            $(this).dialog('close');
                        }
            }]
    });
}    

function deleteConfirmDialogCallback(data, url, loc) {
    $.post( url, { dataIds : JSON.stringify(data) }, function(response) {
            if( $.trim(response) == '' ) {
                alert(strings['TEXT_DELETE_ERROR']);
            } else {
                window.location.replace(loc);
            }
        },
        'json' // I expect a JSON response
    );
}    
