    var table = $('#backlog').DataTable();

    $(document).ready(function() {
        $('#loadingmessage').show();
        $.post('@webContext@/my/tracks/getBacklog', function(response) {
            $('#loadingmessage').hide();
            if( response.length > 0 ) {
                $('#buttonsBacklog').show();
            }
            $('#backlogList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="backlog"></table>' );

            $('#backlog').dataTable( {
                "data": response,
                "scrollY": "400px",
                "scrollCollapse": true,
                "language": { "decimal": ",", "thousands": "." },
                "bStateSave" : true,
                "aoColumnDefs" : [
                    {
                        aTargets: [ 0 ],
                        mRender: function (data,type,full) {   // row, data contains the object and value for the column
                            return '<input type="checkbox" id="' + full.car.id + '" name="backlogId[]" value="' + full.backlogName + '" />';
                        }
                    }
                ],
                "aoColumns": [
                    { "title": "#", "class": "dt-body-center", "data" : "", "orderable" : false },
                    { "title": strings['TEXT_BACKLOG_NAME'], "class": "dt-body-center", "data" : "backlogName", "orderable" : false },
                    { "title": strings['TEXT_BACKLOG_SIZE'], "class": "dt-body-center", "data" : "backlogSize", "orderable" : false },
                    { "title": strings['TEXT_BACKLOG_DATE'], "class": "dt-body-center", "data" : "backlogDate", "orderable" : false },
                    { "data": "car.name", "bVisible" : false, "aDataSort": [ 4 ] }
                ],
                "drawCallback": function ( settings ) {
                    var api = this.api();
                    var rows = api.rows( {page:'current'} ).nodes();
                    var last = null;
                    api.column(4, {page:'current'} ).data().each( function ( group, i ) {
                        if ( last != group ) {
                            $(rows).eq( i ).before(
                                '<tr class="group"><td colspan="5">'+group+'</td></tr>'
                            );
                            last = group;
                        }
                    } );
                }
            } );
        });

        $('#submitBacklog').click( function() {
            $("#backlogForm :input").attr("disabled", true);
            var data = composePairs();
            if( data.length == 0 ) {
                fnOpenAlert(strings['TEXT_EMPTY_BACKLOG_LIST']);
                $("#backlogForm :input").removeAttr('disabled');
            } else {
                $.post( '@webContext@/my/tracks/loadBacklog', { backlog : JSON.stringify(data) }, function(data) {
                        if( $.trim(data) != '' ) {
                            window.location.replace("@webContext@/my/tracks");
                        }
                    },
                    'json' // I expect a JSON response
                );
            }
            return false;
        });

        $('#clearBacklog').click( function() {
            var data = new Array();
            fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_BACKLOG_CONFIRMATION'],"@webContext@/my/tracks/clearBacklog","@webContext@/my/tracks");
            return false;
        });

        $('#clearSelectedBacklog').click( function() {
            $("#backlogForm :input").attr("disabled", true);
            var data = composePairs();
            if( data.length == 0 ) {
                fnOpenAlert(strings['TEXT_EMPTY_BACKLOG_LIST']);
            } else {
                fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_BACKLOG_CONFIRMATION'],"@webContext@/my/tracks/clearBacklog","@webContext@/my/tracks/checkBacklog");
            }
            $("#backlogForm :input").removeAttr('disabled');
            return false;
        });

    });

    function composePairs() {
        var data = [];
        $($("#backlogForm input[type=checkbox]:checked")).each(function() {
            data.push({
                carid : this.id,
                id : $(this).val()
            });
        });
        return data;
    }
