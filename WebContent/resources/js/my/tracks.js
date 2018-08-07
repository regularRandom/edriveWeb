var tracksTable = $('#tracks').DataTable();
var bounds = new google.maps.LatLngBounds();

$(document).ready(function() {
    google.maps.event.addDomListener(window, 'load', initializeGoogle);

    $('#trackActionsDescriptionHolder').hide();
    $('#dialogNewCustomGroup').hide();
    showHideHolders(".showTrackActionsHelp",strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'],strings['TEXT_SHOW_HIDE_TRACK_ACTION_DESCRIPTION'])

    $('#tracksList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="tracks"></table>' );

    tracksTable = $('#tracks').dataTable( {
        "sAjaxSource": "@webContext@/my/tracks/getTracksList",
        "sServerMethod": "POST",
        "sAjaxDataProp" : "",
        "dom": 'l<"tracksToolbar">frtip',
        "initComplete": function() {
            $("div.tracksToolbar").html('&nbsp;<a href="#" class="reloadTracks"><img src="@webContext@/resources/images/reload-icon.png" border="0"/></a>');
        },
        "scrollY": "400px",
        "scrollCollapse": true,
        "language": { "decimal": ",", "thousands": "." },
        "order": [ 2, "desc" ],
        "bStateSave" : true,
        "aoColumnDefs" : [
            {
                aTargets: [0],    // Column number which needs to be modified
                mRender: function( data, type, full ) {   // row, data contains the object and value for the column
                    var readOnly = full.readOnly || full.status == "LOCKED" ? " disabled readonly" : "";
                    var toMerge = full.toMerge != null ? '<img src="@webContext@/resources/images/Arrow-Merge-icon.png" border="0" title="' +
                                                                    full.toMerge +
                                                              '" alt="' +
                                                                    full.toMerge +
                                                              '" style="cursor: pointer; cursor: hand;" data-merge-id="' +
                                                                    full.toMerge +
                                                              '" class="toMerge" data-original-id="' +
                                                                    full.id +
                                                              '"/>' : '';
                    return '<input type="checkbox" id="entityId['+data+']" name="entityId[]" value="'+data+'" ' + readOnly + '/>&nbsp;' + toMerge;
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
                    var readOnly = full.readOnly ? 'checked' : '';
                    var publicTrack = full.publicTrack ? 'checked' : '';
                    var starred = '<img id="' + full.id + '" alt="*" class="switchStarred" src="@webContext@/resources/images/' + (full.starred ? 'star-icon.png' : 'star-empty-icon.png') + '" border="0"/>&nbsp;';
                    var actions = starred +
                            '<input type="checkbox" name="readOnly" id="' + full.id + '" class="trackAction" ' + readOnly + '/>&nbsp;' +
                            '<input type="checkbox" name="publicTrack" id="' + full.id + '" class="trackAction" ' + publicTrack + '/>&nbsp;' +
                            '<a href="#" class="deleteTrack" id="' + full.id + '"><img src="@webContext@/resources/images/button-delete.png" border="0"/></a>';
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
            { "title": strings['TEXT_TRACK_ACTION'] + "<br/><font size=1>" + strings['TEXT_TRACK_READONLY_PUBLIC_DELETE'] + "</font>", "class": "dt-body-center", "orderable" : false }
        ],
        "createdRow": function( row, data, index ) {
            var dataRow = data.name;
            if( data.status == 'ACTIVE' ) {
                dataRow = '<a href="@webContext@/my/tracks/show/' + data.id + '">' + data.name + '</a>';
            } else {
                $('td', row).css('color', 'Orange');
            }
            if( data.description ) {
                dataRow += '&nbsp;<a href="#" title="' + data.description.replace('\"','&ldquo;').replace('\"','&rdquo;') + '"><img src="@webContext@/resources/images/Note-icon.png" border="0"/></a>';
            }
            if( data.groupName != 'nogroup' ) {
                dataRow += '&nbsp;<a href="#" title="' + data.groupName.replace('\"','&ldquo;').replace('\"','&rdquo;') + '"><img src="@webContext@/resources/images/label-icon.png" border="0"/></a>';
            }
            $('td', row).eq(1).html(dataRow);
        }
    } );

    $('#submitMerge').click( function() {
        var button = $('#submitMerge');
        var data = prepareEntitiesList();
        doAction( data, button, "tracks/merge", null, tracksTable );
        return false;
    });

    $('#submitArchive').click( function() {
        var button = $('#submitArchive');
        var data = prepareEntitiesList();
        doAction( data, button, "tracks/archive", null, tracksTable );
        return false;
    });

    $('#submitExtract').click( function() {
        var button = $('#submitExtract');
        var data = prepareEntitiesList();
        doAction( data, button, "tracks/extract", null, tracksTable );
        return false;
    });

    $(document).on('click', 'img.toMerge', function() {
        var data = new Array();
        var button = $('#submitMerge');
        var text = strings['TEXT_MERGE_TRACKS_CONFIRMATION'];
        data.push({
            id : $(this).attr("data-merge-id")
        });
        data.push({
            id : $(this).attr("data-original-id")
        });
        fnOpenDeleteConfirmDialog(data, text.format($(this).attr("data-merge-id"), $(this).attr("data-original-id")) + " ","@webContext@/my/tracks/merge","@webContext@/my/tracks");
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
            fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_BACKLOG_CONFIRMATION'] + " ","@webContext@/my/tracks/deleteTracks","@webContext@/my/tracks");
        }
        return false;
    });

    $('#tracks tbody').on('change', 'input.trackAction', function() {
        updateTrack( $(this).attr('id'), $(this).attr('name'), $(this).is(':checked') );
    });

    $(document).on('click', 'img.switchStarred', function() {
        var starred = ($(this).attr('src').indexOf('empty') > 0 ? true : false);
        updateTrack( $(this).attr('id'), 'starred', starred );
        $(this).attr('src', '@webContext@/resources/images/' + (starred ? 'star-icon.png' : 'star-empty-icon.png'));
    });

    $(document).on('click','a.deleteTrack', function() {
        var data = new Array();
        data.push({
            id : $(this).attr('id')
        });
        fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_TRACK_CONFIRMATION'] + " ","@webContext@/my/tracks/deleteTracks","@webContext@/my/tracks");
    });

    getGroupList( null );

    $(document).on('click','a.reloadTracks', function(e) {
        reloadTable( tracksTable );
    });
});
