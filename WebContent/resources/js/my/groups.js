var groupsTable = $('#groups').DataTable();

$(document).ready(function() {
    $('#groupsList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="groups"></table>' );
    $('#dialogNewCustomGroup').hide();

    groupsTable = $('#groups').dataTable( {
        "sAjaxSource": "@webContext@/my/groups/getGroupsList",
        "sServerMethod": "POST",
        "sAjaxDataProp" : "",
        "dom": 'l<"groupsToolbar">frtip',
        "initComplete": function() {
            $("div.groupsToolbar").html('&nbsp;<a href="#" class="reloadGroups"><img src="@webContext@/resources/images/reload-icon.png" border="0"/></a>');
        },
        "scrollY": "400px",
        "scrollCollapse": true,
        "language": { "decimal": ",", "thousands": "." },
        "bStateSave" : true,
        "aoColumnDefs" : [
            {
                aTargets: [0],    // Column number which needs to be modified
                mRender: function( data, type, full ) {   // row, data contains the object and value for the column
                    return '<input type="checkbox" id="'+full.id+'" name="entityId[]" value="'+full.id+'"/>&nbsp;' + full.id;
                }
            },
            {
                aTargets: [1],
                mRender: function( data, type, full ) {
                    return "<span style='display:none;'>" + full.name + ", " + full.description + "</span>" + full.name;
                }
            },
            {
                aTargets: [4],    // Column number which needs to be modified
                mRender: function( data, type, full ) {   // row, data contains the object and value for the column
                    return '<a href="#" class="deleteGroup" id="' + full.id + '"><img src="@webContext@/resources/images/button-delete.png" border="0"/></a>';
                }
            }

        ],
        "aoColumns": [
            { "title": "#", "class": "dt-body-center", "data" : "id", "orderable" : false, "sWidth" : "10%" },
            { "title": strings['TEXT_GROUP_NAME'], "class": "dt-body-right", "data" : "name", "orderable" : true, "sWidth" : "70%" },
            { "title": strings['TEXT_OBJECTS_COUNT'], "class": "dt-body-center", "data" : "objectsCount", "orderable" : true, "sWidth" : "10%" },
            { "data" : "description", "bVisible" : false, "aDataSort": [ 3 ] },
            { "title": strings['TEXT_GROUP_ACTION'] + "<br/><font size=1>" + strings['TEXT_GROUP_DELETE'] + "</font>", "class": "dt-body-center", "orderable" : false, "sWidth" : "10%" }
        ],
        "drawCallback": function ( source ) {
            var api = this.api();
            var rows = api.rows( {page:'current'} ).nodes();
            var last = null;
            api.column(3, {page:'current'} ).data().each( function ( description, i ) {
                if ( last != i ) {
                    $(rows).eq( i ).after(
                        '<tr><td colspan="3" align="right" style="font-size:8pt;">' + description + '</td><td>&nbsp;</td></tr>'
                    );
                    last = i;
                }
            });
        }
    } );

    $('#addNewCustomGroup').click( function() {
        var button = $('#addNewCustomGroup');
        fnOpenNewCustomGroupDialog(0);
        return false;
    });

    $('#clearSelectedGroups').click( function() {
        var button = $('#clearSelectedGroups');
        var data = prepareEntitiesList();
        doAction( data, button, "groups/clearGroups", null, groupsTable );
        return false;
    });

    $(document).on('click','a.reloadGroups', function(e) {
        reloadTable( groupsTable );
    });

    $(document).on('click','a.deleteGroup', function() {
        var data = new Array();
        data.push({
            id : $(this).attr('id')
        });
        fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_GROUP_CONFIRMATION'] + " ","@webContext@/my/groups/deleteGroups","@webContext@/my/groups");
    });

    $('#deleteGroups').click( function() {
        var button = $('#deleteGroups');
        var data = prepareEntitiesList();
        if( data.length == 0 ) {
            fnOpenAlert(strings['TEXT_EMPTY_GROUPS_LIST']);
            button.removeAttr('disabled');
        } else {
            fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_GROUP_CONFIRMATION'] + " ","@webContext@/my/groups/deleteGroups","@webContext@/my/groups");
        }
        return false;
    });

});
