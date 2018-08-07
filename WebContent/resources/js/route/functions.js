$(document).ready(function() {
    google.maps.event.addDomListener(window, 'load', initialize);

    function initialize() {
        var mapOptions = {
            zoom: DEFAULT_ZOOM,
            center: DEFAULT_CENTER,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        
        map = new google.maps.Map(document.getElementById("planningMap"),
                        mapOptions);
        locationsArray = new Array();
        flightPlanCoordinates = new Array();
        bounds = new google.maps.LatLngBounds();
        setStyle(map);
        $('#loadingmessage').hide();
    }

    function markLocation(pId,pLat,pLon,pPlace,pRegion) {
        var latLng = new google.maps.LatLng(pLat, pLon);
        var lMarker = new google.maps.Marker({
            position: latLng,
            map: map,
            title: pPlace
        });
        map.setCenter(latLng);
        map.setZoom(CUSTOM_ZOOM);
        var location = {
                geoid: pId, 
                lat: pLat, 
                lon: pLon, 
                marker: lMarker, 
                place: pPlace,
                region: pRegion
            };
        if( pId !== null ) {
            locationsArray.push(location);
        } else {
            locationsArray[0] = location;
        }
    }

    function updateLocationsArray(id) {
        for( var i=0; i < locationsArray.length; i++ ) {
            if (locationsArray[i].geoid == id) {
                locationsArray[i].marker.setMap(null);
                locationsArray.splice(i,1);
            }
        }
        return locationsArray;
    }

    function toLocationSearchDto() {
        var locationsArrayDto = [];
        for( var i=0; i < locationsArray.length; i++ ) {
            var locationDto = {
                    geoid: locationsArray[i].geoid,
                    lat: locationsArray[i].lat,
                    lon: locationsArray[i].lon,
                    region: locationsArray[i].region
                };
            locationsArrayDto.push(locationDto);
        }
        return locationsArrayDto;
    }

    $(function() {
        $('#calculateDirectrions').click(function( data ){
            $('#loadingmessage').show();
            $.post( '@webContext@/route/calculateDirectrions', { q : JSON.stringify( toLocationSearchDto() ) }, function(response) {
                        switch($.trim(response.code)) {
                            case 'Ok':
                                var routes = response.routes;
                                var distances = new Array();
                                for( var i = 0; i<routes.length; i++ ) {
                                    var route = routes[i].waypoints;
                                    for( var j = 0; j<route.length; j++ ) {
                                        flightPlanCoordinates.push(new google.maps.LatLng(route[j].latitude, route[j].longitude));
                                        bounds.extend(flightPlanCoordinates[j]);
                                    }
                                    distances.push(routes[i].distance);
                                }
                                loadMap("planningMap");
                                helpInstructions = $(".routePlanningInstructions").text();
                                var place = '';
                                for( var i = 0; i<locationsArray.length; i++ ) {
                                    place = place + '<li>' + locationsArray[i].place;
                                }
                                var speedDivider = strings['speedDivider'];
                                $('.routePlanningInstructions').html(strings['TEXT_OSRM_ROUTE'] +
                                       round(distances[0]/speedDivider,2) +
                                       strings['MILEAGE_UNIT'] +
                                       place +
                                       '</ol>');
                                break;
                            default:
                                alert(strings['TEXT_OSRM_ROUTE_NOT_FOUND_ERROR']);
                                break;
                        }
                        $('#loadingmessage').hide();
                    },
                    'json' // I expect a JSON response
                );
        });

    // clear search form
    $('#clearDirectrions').click(function() {
        for( var i=0; i < locationsArray.length; i++ ) {
            var marker = locationsArray[i].marker;
            marker.setMap(null);
        }
        $("form#prepareLocationSearch :input").each(function(){
            $(this).val("");
        });
        $('form#prepareLocationSearch').find('input[type=text]').filter(':visible:first').attr("id","firstLocationSearch");
            helpInstructions ? $('.routePlanningInstructions').html(helpInstructions) : null;
            $('*[class^="toRemove"]').each(function(){
                $(this).remove();
            });
        if( flightPath != null ) {
            flightPath.setMap(null);
        }
        $("#select0").val($("#select0 option:first").val());
            initialize();
        });

    $('input[name="locationSearch[]"]').each(function() {
        var options = {
            source: function( request, response ) {
                var data = $.ajax({
                    url: '@webContext@/route/findLocation',
                    dataType: "json",
                    method: "POST",
                    data: {
                        q: request.term
                    },
                    success: function( data ) {
                        var out = [];
                        $.each(data, function (i, a) {
                            a.label = a.name.replace('<b>','').replace('</b>','') + ", " + a.district.replace('<b>','').replace('</b>','') + ", " + a.country;
                            a.id = a.geoid;
                            out.push(a);
                        });
                        response(out);
                    }
                });
            },
            minLength: 3,
            select: function( event, ui ) {
                markLocation(ui.item.geoid,ui.item.lat,ui.item.lon,ui.item.value,ui.item.region);
                $(this).attr('id',ui.item.geoid);
                return false;
            },
            open: function() {
                $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
            },
            close: function() {
                $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
            }
        };

        $('body').on("keydown.autocomplete", 'input[name="locationSearch[]"]', function() {
            $(this).autocomplete(options);
        });

        $('body').off("keyup.autocomplete").on("keyup.autocomplete", 'input[name="locationSearch[]"]', function() {
            if( !this.value ) {
                updateLocationsArray($(this).attr('id'));
            }
        });

    });

    $("#planningActionRow").click(function() {
        var $clone = $("#planningActionRow").clone();
        $clone.find('td').eq(0).html('<img src="@webContext@/resources/images/User-Interface-Minus-icon.png" border="0"/>');
        $clone.addClass('planningActionRowRemove');
        $clone.attr('class','toRemove');
        $clone.insertAfter("tr:last");

        $clone = $("#locationSearchRow").clone();
        $clone.find('input').val(null).prop("id","newLocationRow");
        $clone.attr('class','toRemove2');
        $clone.insertAfter("tr:last");
        $clone.find('input[name="locationSearch[]"]').focus();
    });

    $(document).on("click",".toRemove",function(e) {
        var id = $(this).closest('tr').next('tr').find('td').eq(1).find('input').attr('id');
        updateLocationsArray(id);
        $(this).closest('tr').next('tr').remove();
        $(this).remove();
        e.preventDefault();
    });

    $(document).on("change","#select0",function(e) {
            var accountId = $("#select0 option:selected").val();
            if( accountId != 0 ) {
            $.post( '@webContext@/my/cars/getCurrentPosition', { accountId : accountId }, function(response) {
                        $("#firstLocationSearch").val(round(response[0].latitude,6) + ", " + round(response[0].longitude,6) + ", " + response[1]);
                        markLocation(null,response[0].latitude,response[0].longitude,response[0].geoTimeZone.name);
                    },
                    'json' // I expect a JSON response
                );
            }
        });
    });
});
