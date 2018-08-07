function initializeGoogle() {
    var mapOptions = {
       mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    if( document.getElementById("mapCarLocationInFullScreen") !== null ) {
        map = new google.maps.Map(document.getElementById("mapCarLocationInFullScreen"),mapOptions);
    }
}

function doAction( data, button, endPoint, group, tableToReload ) {
    button.attr('disabled','disabled');
    if( data.length == 0 ) {
        fnOpenAlert(strings['TEXT_EMPTY_TRACK_LIST']);
    } else {
        $.post( '@webContext@/my/' + endPoint, { dataIds : JSON.stringify(data), group : JSON.stringify(group) }, function(data) {
                if( $.trim(data) != '' ) {
                    reloadTable( tableToReload );
                }
            },
            'json' // I expect a JSON response
        );
    }
    button.removeAttr('disabled');
}

function reloadTable( tableToReload ) {
    tableToReload.api().ajax.reload();
}

function getGroupList( data ) {
    $.post('@webContext@/my/groups/getGroupsList', function(response) {
        $('#customGroupSelect').children('option:not(:first)').remove();
        $.each(response,function(key, value) {
            $('#customGroupSelect').append('<option value=' + value.id + '>' + value.name + ' (' + value.objectsCount + ')</option>');
        });
        $("#customGroupSelect").val(data == null ? 0 : data );
    });
}

function populateMakes() {
    $('#carMake').find('option')
        .remove()
        .end()
        .append('<option>' + strings['TEXT_ACCOUNT_ADD_CAR'] + '</option>');
    var option;
    for( var i=0; i<makes.length; i++ ) {
        option += '<option value="'+ i + '">' + makes[i].name + '</option>';
    }
    $('#carMake').append(option);
}

function initializeFieldsForMyCars(pId) {
    var myCar = myCars.getItemByParam({"accountId": pId});
    $('#carEditName').html(myCar.name);
    var generation = makes.getItemByParam({"id": myCar.carId}).model.getItemByParam({"id": myCar.modelId}).generation.getItemByParam({"id": myCar.generationId});
    populateYearOfProduction( generation,'Edit', pId );
    $('#carCurrentMileage').val(myCar.currentMileage);
}

function getCarsList() {
    if( makes.length == 0 ) {
        var ajax = $.ajax({
            url: "@webContext@/my/cars/getCarsList",
            method: "POST",
            dataType: "json",
            success: function(response) {
                makes = $.map(response, function(el) {return el}); 
                populateMakes();
                startingPoint.resolve();
            },
            error: function() {
                alert("error");
                return false;
            }
        });
    }
    return startingPoint.promise();
}

function getMyCars(id) {
    var ajax = $.Deferred();
    if( myCars.length == 0 ) {
        $.ajax({
            url: "@webContext@/my/cars/getMyCars",
            method: "POST",
            dataType: "json",
            success: function(response) {
                myCars = $.map(response, function(el) {return el} );
                ajax.resolve();
            },
            error: function() {
                alert("error");
                return false;
            }
        });
    } else {
        ajax.resolve();
    }
    $.when(ajax).then(function() {initializeFieldsForMyCars(id);});
}

function populateYearOfProduction( generation, addEdit, currentCar ) {
    var myCar = findMyCar(currentCar);
    $('#carYearOfProduction' + addEdit).find('option')
        .remove()
        .end();
    var option = '';
    yearOfProduction = getYearsToCurrent( generation.startYear, generation.endYear );
    for( var i=yearOfProduction.length-1; i>=0; i-- ) {
        option += '<option value="'+ yearOfProduction[i] + '"' + ((myCar !== null && myCar.yearOfProduction == yearOfProduction[i]) ? ' selected>' : '>') + yearOfProduction[i] + '</option>';
    }
    $('#carYearOfProduction' + addEdit).append(option);
    $('#carYearOfProductions' + addEdit).show();
    datePicker((myCar === null ? 'Add' : 'Edit'),currentCar);
}

function datePicker( addEdit, currentCar ) {
    var myCar = findMyCar(currentCar);
    $('#carDateOfPurchase'+addEdit).datepicker({ dateFormat: 'dd.mm.yy' });
    $('#carDateOfPurchase'+addEdit).datepicker('option', { maxDate: '0' });
    $('#carDateOfPurchase'+addEdit).datepicker('setDate',((myCar !== null) ? myCar.dateOfPurchase : null));
    var dateOfPurchase = $('#carDateOfPurchase'+addEdit).datepicker('getDate');
}

function findMyCar( currentCar ) {
    var myCar = null;
    if( currentCar !== undefined ) {
        myCar = myCars.getItemByParam({"accountId": currentCar});
    }
    return myCar;
}

function saveMyNewCar() {
    var dateOfPurchase = $('#carDateOfPurchaseAdd').datepicker('getDate');
    myNewCarDto.dateOfPurchase = $.datepicker.formatDate("dd.mm.yy", dateOfPurchase);
    myNewCarDto.yearOfProduction = $('#carYearOfProductionAdd option:selected').val();
    myNewCarDto.initialMileage = $('#carInitialMileage').val();
    myNewCarDto.vinCode = $('#carVinCode').val();
    if( !validateForm(myNewCarDto) ) {
        alert(strings['TEXT_ALL_FIELDS_MUST_BE_FILLED']);
        return false;
    }
    $.post( '@webContext@/my/cars/addCar', { myNewCar : JSON.stringify( myNewCarDto ) }, function(response) {
            if( $.trim(response) == 'ERROR' ) {
                alert("error");
                return false;
            } else {
                location.reload();
            }
        },
        'json' // I expect a JSON response
    );
}

function saveMyCarForUpdate(pId) {
    var dateOfPurchase = $('#carDateOfPurchaseEdit').datepicker('getDate');
    var lDateOfPurchase = $.datepicker.formatDate("dd.mm.yy", dateOfPurchase);
    var lYearOfProduction = $('#carYearOfProductionEdit option:selected').val();
    var lCurrentMileage = $('#carCurrentMileage').val();
    var myCarForUpdateDto = { accountId: pId, yearOfProduction: lYearOfProduction, dateOfPurchase: lDateOfPurchase, currentMileage: lCurrentMileage };
    if( !validateForm(myCarForUpdateDto) ) {
        alert(strings['TEXT_ALL_FIELDS_MUST_BE_FILLED']);
        return false;
    }
    $.post( '@webContext@/my/cars/editCar', { myCar : JSON.stringify( myCarForUpdateDto ) }, function(response) {
            if( $.trim(response) == 'ERROR' ) {
                alert("error");
                return false;
            } else {
                location.reload();
            }
        },
        'json' // I expect a JSON response
    );
}

function validateForm(dto) {
    for (var property in dto) {
        if( dto.hasOwnProperty(property) && ( dto[property] == null || dto[property] == "" ) ) {
            return false;
        }
    }
    return true;
}

function getCarLocation(pId,callback) {
    var location = $.post( '@webContext@/my/cars/getCurrentPosition', { accountId : pId }, function(response) {
                if( $.trim(response) == 'ERROR' ) {
                    alert("error");
                    return false;
                } else {
                    callback(response);
                }
            },
            'json'
        );
}

function fnCarLocationInFullScreen(location) {
    startLatlng = new google.maps.LatLng(location[0].latitude, location[0].longitude);
    var wWidth = $(window).width();
    var dWidth = wWidth * 0.9;
    var wHeight = $(window).height();
    var dHeight = wHeight * 0.9;
    $("#mapCarLocationInFullScreen").dialog({
        resizable: false,
        modal: true,
        height: dHeight,
        width: dWidth
    });

    var lMap = loadMap("mapCarLocationInFullScreen",LOCATION_ZOOM);
    var fullScreenMarker = setMarker(lMap,startLatlng,null);
    lMap.setCenter(fullScreenMarker.getPosition());
    lMap.setZoom(LOCATION_ZOOM);
}

function showHideHolders(what,show,hide) {
    $(what).click(function() {
        //get collapse content selector
        var collapse_content_selector = $(this).attr('href');
        //make the collapse content to be shown or hide
        var toggle_switch = $(this);
        $(collapse_content_selector).toggle(function(){
            if($(this).css('display')=='none'){
                //change the button label to be 'Show'
                toggle_switch.html(show);
            } else {
                //change the button label to be 'Hide'
                toggle_switch.html(hide);
            }
        });
    });
}

function fnOpenNewCustomGroupDialog(groupId) {
    var buttonYes = 'OK';
    $("#dialogNewCustomGroup").dialog({
        resizable: false,
        modal: true,
        title: "New custom group",
        height: 350,
        width: 400,
        buttons: [{
                text : buttonYes,
                click : function () {
                            customGroupDto.name = $('#customGroupName').val();
                            customGroupDto.description = $('#customGroupDescription').val();
                            customGroupDto.type = "TRACK";
                            return callbackNewCustomGroup(customGroupDto);
                        }
            }]
    });
}

function callbackNewCustomGroup( customGroupDto ) {
    if( customGroupDto.name == '' ) {
        $("#addCustomGroupErrorField").html(strings['TEXT_GROUP_NAME_CANNOT_BE_EMPTY']);
        return false;
    }
    $.post( '@webContext@/my/groups/addCustomGroup', { group : JSON.stringify(customGroupDto) }, function(data) {
            if( $.trim(data) == '' ) {
                alert(strings['TEXT_ADD_CUSTOM_GROUP_ERROR']);
            } else {
                $('#addCustomGroup').trigger("reset");
                $("#dialogNewCustomGroup").dialog('close');
                getGroupList( data );
                customGroupDto.id = data;
                reloadTable( groupsTable );
                return customGroupDto;
            }
        },
        'json' // I expect a JSON response
    );
}

function prepareEntitiesList() {
    var data = new Array();
    $("input[name='entityId[]']:checked").each(function(i) {
        data.push({
            id : $(this).val()
        });
    });
    return data;                
}

function updateTrackDescription( t, d ) {
   $.post( '@webContext@/my/tracks/updateDescription', { trackId : t, description : d }); 
}

function updateTrack( t, f, v ) {
    $.post( '@webContext@/my/tracks/update', { trackId : t, field : f, value : v }); 
}

function fnMapInFullScreen() {
    var wWidth = Math.round($(window).width()*0.9);
    var wHeight = Math.round($(window).height()*0.9);
    $("#mapInFullScreen").dialog({
        resizable: false,
        modal: true,
        height: wHeight,
        width: wWidth
    });
    var lMap = loadMap("mapInFullScreen",DEFAULT_ZOOM);
    fullScreenMarker = setMarker(lMap,startLatlng,strings['TEXT_TRACK_START']);
}

String.prototype.format = function(){
    var a = this, b;
    for(b in arguments){
        a = a.replace(/%[a-z]/,arguments[b]);
    }
    return a; // Make chainable
};
