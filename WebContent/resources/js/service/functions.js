$(document).ready(function() {
    $("#insertUpdateTranslationDialog").hide();
    $(document).on('click','a.insertUpdateTranslation', function() {
        insertUpdateTranslationDialog($(this).attr('id'));
    });

    function insertUpdateTranslationDialog( updateId ) {
        document.getElementById('translationForm').reset();
        $("#translationErrorField").empty();
        $("#insertUpdateTranslationDialog").show();

        if( updateId !== 'new' ) {
            $.post( "@webContext@/service/getTranslation", { editId : updateId }, function(response) {
                    if( $.trim(response) == 'ERROR' ) {
                        $("#translationErrorField").html(strings['TEXT_UPDATE_TRANSLATION_ERROR']);
                    } else {
                        $("#textEntityKey").val(response[0].key);
                        for( var i in response ) {
                            if( response[i] && response[i].translation )
                                $('input[id='+response[i].translation.language+']').val(response[i].translation.value);
                        }
                    }
                },
                'json' // I expect a JSON response
            );
        }

        var button = strings['TEXT_SAVE'];
        $("#insertUpdateTranslationDialog").dialog({
            resizable: false,
            modal: true,
            title: strings['TEXT_TEXT_ENTITY_TRANSLATION'],
            height: 420,
            width: 410,
            buttons: [{
                text: button,
                click : function () {
                    var translationsArray = Array.prototype.slice.call(document.getElementsByClassName("translationEntity"));
                    var textEntityKey = document.getElementsByClassName("textEntityKey")[0];
                    var textEntityDtos = new Array();
                    for( var i = 0; i<translationsArray.length; i++ ) {
                        var translationEntityDto = {
                            language: translationsArray[i].name,
                            value: translationsArray[i].value
                        }
                        var textEntityDto = {
                            key : textEntityKey.value,
                            translation : translationEntityDto
                        };
                        textEntityDtos.push(textEntityDto);
                    }
                    insertUpdateTranslationCallback(textEntityDtos,updateId);
                }
            }]
        });
    }    

    function insertUpdateTranslationCallback( dtos, updateId ) {
        if( validateTranslationFields() && document.getElementsByClassName("textEntityKey")[0].value != '' ) {
            $.post( "@webContext@/service/saveTranslation", { translation : JSON.stringify(dtos), update : updateId }, function(response) {
                    if( $.trim(response) == 'ERROR' ) {
                        $("#translationErrorField").html(strings['TEXT_INSERT_TRANSLATION_ERROR']);
                    }
                },
                'json' // I expect a JSON response
            );
        location.reload();
        } else {
            $("#translationErrorField").html(strings['TEXT_FORM_TRANSLATION_ERROR']);
        }
    }

    function validateTranslationFields() {
        var translationEntityArray = Array.prototype.slice.call(document.getElementsByClassName("translationEntity"));
        for( var i in translationEntityArray ) {
            if( translationEntityArray[i].value == '' ) {
                return false;
            }
        }
        return true;
    }

    $('#loadingmessage1').show();
    $.post("@webContext@/service/getTranslations", function(response) {

        $('#loadingmessage1').hide();
        $('#translationsList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="translations"></table>' );

        var group = null;

        $('#translations').dataTable( {
            "data": response,
            "scrollY": "300px",
            "scrollCollapse": true,
            "language": { "decimal": ",", "thousands": "." },
            "DT_RowId": "key",
            "bStateSave" : true,
            "aoColumns": [
                { "data": "key", "bVisible" : false, "aDataSort": [ 0 ] },
                { "data": "id", "bVisible" : false, "aDataSort": [ 1 ] },
                { "title": strings['TEXT_TEXT_LANGUAGE'], "class": "dt-body-center", "data" : "translation.language", "orderable" : false, "width": "20%" },
                { "title": strings['TEXT_TEXT_ENTITY_VALUE'], "class": "dt-body-center", "data" : "translation.value", "orderable" : false }
            ],
            "fnCreatedRow": function (nRow, aData, iDataIndex) {
                $(nRow).attr('id', aData.id); // or whatever you choose to set as the id
            },
            "drawCallback": function ( data ) {
                var groupName = null;
                var api = this.api();
                var rows = api.rows( {page:'current'} ).nodes();
                api.column(0, {page:'current'} ).data().each( function ( data, index ) {
                    if ( groupName != data ) {
                        var id = $(rows).eq(index).attr('id');
                        $(rows).eq( index ).before(
                            '<tr class="group"><td colspan="2" width="90%">' + data + '</td><td><a href="#" id="' + id + '" class="insertUpdateTranslation"><img src="@webContext@/resources/images/button-edit.png" border="0"/></a>&nbsp;<a href="#" class="deleteTranslation" id="' + id + '"><img src="@webContext@/resources/images/button-delete.png" border="0"/></a></td></tr>'
                        );
                        groupName = data;
                    }
                });
            }
        });

        $(document).on('click','a.deleteTranslation', function() {
            var data = new Array();
            data.push({
                id : $(this).attr('id')
            });
            fnOpenDeleteConfirmDialog(data,strings['TEXT_DELETE_TRANSLATION_CONFIRMATION'],'@webContext@/service/deleteTranslation','@webContext@/service');
        });
    });
});
