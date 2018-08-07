var makes = new Array();
var models = new Array();
var generations = new Array();
var myCars = new Array();
var carLocations = new Array();

var myNewCarDto = { makeId: null, modelId: null, generationId: null, yearOfProduction: null, dateOfPurchase: null, initialMileage: null, vinCode: null };
var myCarDto = { accountId: null, ownerId: null, carId: null, modelId: null, generationId: null, name: null, yearOfProduction: null, dateOfPurchase: null, initialMileage: null, currentMileage: null, vinCode: null, password: null, location: { country: null, latitude: null, longitude: null } };
var customGroupDto = { id: null, customerId: null, name: null, type: null, description: null };

var startingPoint=$.Deferred();
var startLatlng;

var LOCATION_ZOOM=15;

var flightPlanCoordinates = [];
var gMap;
var floatingMarker, fullScreenMarker;

$('#carDateOfPurchaseAdd').datepicker({ 
                           'defaultDate':'+0',
                           'changeMonth':true,
                           'changeYear':true
                        });

$('#carDateOfPurchaseEdit').datepicker({ 
                           'defaultDate':'+0',
                           'changeMonth':true,
                           'changeYear':true
                        });
