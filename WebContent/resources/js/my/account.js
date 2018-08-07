$(document).ready(function() {
    google.maps.event.addDomListener(window, 'load', initializeGoogle);

    $(".carLocation").each(function() {
        var carLocation = { location: null, id: null, name: null };
        var a = $(this);
        var id = $(this).attr("id");
        var location = JSON.stringify(getCarLocation(id,function(data) {
            carLocation.location = data;
            carLocation.id = id;
            carLocations.push(carLocation);
            a.text(data[1] + ", " + round(data[0].latitude,6) + ", " + round(data[0].longitude,6));
        }));
    });

    $(".carLocation").on('click', function() {
        var id = $(this).attr("id");
        fnCarLocationInFullScreen(carLocations.getItemByParam({"id": id}).location);
    });

});
