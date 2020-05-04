var tracksTable = $('#tracks').DataTable();
var bounds = new google.maps.LatLngBounds();

$(document).ready(function() {
    google.maps.event.addDomListener(window, 'load', initializeGoogle);

    $('#trackActionsDescriptionHolder').hide();
    $('#dialogNewCustomGroup').hide();
    showHideHolders(".showTrackActionsHelp",strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'],strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'])

    $('#tracksList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="tracks"></table>' );

    tracksTable = $('#tracks').dataTable( {
        "sAjaxSource": "/edrive/my/tracks/archived/list",
        "sServerMethod": "POST",
        "sAjaxDataProp" : "",
        "dom": 'l<"tracksToolbar">frtip',
        "scrollY": "400px",
        "scrollCollapse": true,
        "language": { "decimal": ",", "thousands": "." },
        "order": [ 2, "desc" ],
        "bStateSave" : true,
        "aoColumnDefs" : [
            {
                aTargets: [0],    // Column number which needs to be modified
                mRender: function( data, type, full ) {   // row, data contains the object and value for the column
                    return '<input type="checkbox" id="entityId['+data+']" name="entityId[]" value="'+data+'" ' + '/>';
                }
            },
            {
                aTargets: [2],
                mRender: function( data, type, full ) {
                    return "<span style='display:none;'>" + full.trackDateHidden + "</span>" + full.trackDate;
                }
            },
            {
                aTargets: [6],    // Column number which needs to be modified
                mRender: function( data, type, full ) {   // row, data contains the object and value for the column
                    var starred = '<img id="' + full.id + '" alt="*" class="switchStarred" src="/edrive/resources/images/' + (full.starred ? 'star-icon.png' : 'star-empty-icon.png') + '" border="0"/>&nbsp;';
                    var actions = starred +
                            '<a href="#" class="deleteTrack" id="' + full.id + '"><img src="/edrive/resources/images/button-delete.png" border="0"/></a>';
                    return actions;
                }
            }
        ],
        "aoColumns": [
            { "title": "#", "class": "dt-body-center", "mData" : "id", "orderable" : false },
            { "title": strings['TEXT_TRACK_NAME'], "class": "dt-body-center", "data" : "name", "orderable" : false, "mData" :  function ( source, type, val ) { return (source.starred ? "*" : "") + source.name + "," + source.description + "," + source.groupName; } },
            { "title": strings['TEXT_TRACK_DATE'], "class": "dt-body-center", "orderable" : true },
            { "title": strings['TEXT_TRACK_DISTANCE'] + ", " + strings['MILEAGE_UNIT'], "class": "dt-body-center", "data" : "distance", "orderable" : true },
            { "title": strings['TEXT_AVG_SPEED']  + ", " + strings['UNIT_OF_SPEED'], "class": "dt-body-center", "data" : "speed", "orderable" : true },
            { "title": strings['TEXT_TRACK_CAR'], "class": "dt-body-center", "mData" : function ( source, type, val ) { 
                       var retVal = null;
                       switch( source.carName ) {
                          case "PEDESTRIAN" : retVal = strings['TEXT_TRACK_PEDESTRIAN']; break;
                          case "BICYCLE" : retVal = strings['TEXT_TRACK_BICYCLE']; break;
                          default: retVal = source.carName; break;
                       }
                       return retVal; }, "orderable" : false },
            { "title": strings['TEXT_TRACK_ACTION'] + "<br/><font size=1>" + strings['TEXT_DELETE_TRACKS'] + "</font>", "class": "dt-body-center", "orderable" : false }
        ],
        "createdRow": function( row, data, index ) {
            var dataRow = data.name;
            $('td', row).css('color', 'Orange');
            if( data.description ) {
                dataRow += '&nbsp;<a href="#" title="' + data.description.replace('\"','&ldquo;').replace('\"','&rdquo;') + '"><img src="/edrive/resources/images/Note-icon.png" border="0"/></a>';
            }
            if( data.groupName != 'nogroup' ) {
                dataRow += '&nbsp;<a href="#" title="' + data.groupName.replace('\"','&ldquo;').replace('\"','&rdquo;') + '"><img src="/edrive/resources/images/label-icon.png" border="0"/></a>';
            }
            $('td', row).eq(1).html(dataRow);
        }
    } );

    $('#submitExtract').click( function() {
        var button = $('#submitExtract');
        var data = prepareEntitiesList();
        doAction( data, button, "tracks/extract", null, tracksTable );
        return false;
    });

    $('#submitAddToCustomGroup').click( function() {
        var button = $('#submitAddToCustomGroup');
        var groupId = $('#customGroupSelect :selected').val();
        var data = prepareEntitiesList();
        if( groupId == 0 ) {
            group = fnOpenNewCustomGroupDialog(groupId);
        } else {
            doAction( data, button, "groups/addToGroup", groupId, tracksTable );
        }
        return false;
    });

    $('#submitDelete').click( function() {
        var button = $('#submitDelete');
        var data = prepareEntitiesList();
        if( data.length == 0 ) {
            fnOpenAlert(strings['TEXT_EMPTY_TRACK_LIST']);
            button.removeAttr('disabled');
        } else {
            fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] + " ","/edrive/my/tracks/deleteTracks","/edrive/my/tracks/archived");
        }
        return false;
    });

    $(document).on('click', 'img.switchStarred', function() {
        var starred = ($(this).attr('src').indexOf('empty') > 0 ? true : false);
        updateTrack( $(this).attr('id'), 'starred', starred );
        $(this).attr('src', '/edrive/resources/images/' + (starred ? 'star-icon.png' : 'star-empty-icon.png'));
    });

    $(document).on('click','a.deleteTrack', function() {
        var data = new Array();
        data.push({
            id : $(this).attr('id')
        });
        fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_TRACK_CONFIRMATION'] + " ","/edrive/my/tracks/deleteTracks","/edrive/my/tracks/archived");
    });

    getGroupList( null );
});
