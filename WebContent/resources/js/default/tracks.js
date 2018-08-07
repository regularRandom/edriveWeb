$(document).ready(function() {
    $('.loadingmessage').show();

    $.post('@webContext@/tracks/getPublicTracksList', function(response) {
        $('.loadingmessage').hide();
        $('#tracksList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="tracks"></table>' );
        $('#tracks').dataTable( {
            "data": response,
            "scrollY": "400px",
            "scrollCollapse": true,
            "language": { "decimal": ",", "thousands": "." },
            "order": [ 1, "desc" ],
            "bStateSave" : true,
            "aoColumns": [
                { "title": strings['TEXT_TRACK_NAME'], "class": "dt-body-center", "data" : "name", "orderable" : false },
                { "title": strings['TEXT_TRACK_DATE'], "class": "dt-body-center", "data" : "trackDate", "orderable" : true,
                  mRender: function (data, type, full) {
                        var dtStart = new Date(data);
                        var dtStartWrapper = moment(dtStart);
                        return dtStartWrapper.format('DD.MMM.YYYY HH:mm:ss');
                    }
                },
                { "title": strings['TEXT_TRACK_DISTANCE'] + ", " + strings['MILEAGE_UNIT'], "class": "dt-body-center", "data" : "distance", "orderable" : true },
                { "title": strings['TEXT_AVG_SPEED']  + ", " + strings['UNIT_OF_SPEED'], "class": "dt-body-center", "data" : "avgSpeed", "orderable" : true }
            ],
            "createdRow": function( row, data, index ) {
                var dataRow = '<a href="@webContext@/tracks/show/' + data.id + '">' + data.name + '</a>';
                if( data.description ) {
                    dataRow += '&nbsp;<a href="#" title="' + data.description.replace('\"','&ldquo;').replace('\"','&rdquo;') + '"><img src="@webContext@/resources/images/Note-icon.png" border="0"/></a>';
                }
                $('td', row).eq(0).html(dataRow);
            }
        } );
    });
});
