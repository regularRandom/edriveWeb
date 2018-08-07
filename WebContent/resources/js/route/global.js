var locationsArray;
var flightPlanCoordinates;
var flightPath;
var startLatlng;
var bounds;
var map = null;
var DEFAULT_CENTER = new google.maps.LatLng(48.721962, 31.596044);  // geographical center of map
var helpInstructions = null;
var startingPoint = $.Deferred();
