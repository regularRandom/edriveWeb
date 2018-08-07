<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

        $("#insertUpdateConfigurationDialog").hide();
        $(document).on('click','a.insertUpdateConfiguration', function() {
            insertUpdateConfigurationDialog($(this).attr('id'));
        });

        <tiles:insertAttribute name="insertUpdateConfigurationDialog"/>

        $('#loadingmessage2').show();
        $.post('<c:url value="${request.getRequestURL()}/service/getConfigurations"/>', function(response) {
            $('#loadingmessage2').hide();
            $('#configurationList').html( '<table cellpadding="2" cellspacing="0" border="0" class="hover" id="configuration"></table>' );

            $('#configuration').dataTable( {
                "data": response,
                "scrollY": "300px",
                "scrollX": "400px",
                "scrollCollapse": true,
                "language": { "decimal": ",", "thousands": "." },
                "bStateSave" : true,
                "aoColumnDefs" : [
                    {
                        aTargets: [3],    // Column number which needs to be modified
                        mRender: function (data,type,full) {   // row, data contains the object and value for the column
                            return '<a href="#" id="' + full.key + '" class="insertUpdateConfiguration"><img src="<c:url value="/resources/images/button-edit.png"/>" border="0"/></a>&nbsp;<a href="#" class="deleteConfiguration" id="' + full.key + '"><img src="<c:url value="/resources/images/button-delete.png"/>" border="0"/></a>';
                        }
                    }
                ],
                "aoColumns": [
                    { "title": "<spring:message code="TEXT_CONFIGURATION_KEY"/>", "class": "dt-body-center", "data" : "key", "orderable" : true, "width": "20%" },
                    { "title": "<spring:message code="TEXT_CONFIGURATION_VALUE"/>", "class": "dt-body-center", "data" : "value", "orderable" : false, "width": "30%" },
                    { "title": "<spring:message code="TEXT_CONFIGURATION_DESCRIPTION"/>", "bVisible": false, "class": "dt-body-left", "data" : "description", "orderable" : false, "sClass": "smallDataTableStyle" },
                    { "title": "", "sClass": "rightAlign", "data" : "", "orderable" : false, "width": "10%" }
                ],
                "drawCallback": function ( data ) {
                    var api = this.api();
                    var rows = api.rows( {page:'current'} ).nodes();
                    var last = null;
                    api.column(2, {page:'current'} ).data().each( function ( group, i ) {
                        if ( last != group ) {
                            $(rows).eq( i ).after(
                                '<tr class="group"><td colspan="2" align="right">' + group + '</td><td></td></tr>'
                            );
                            last = group;
                        }
                    } );
                }

            } );

            $(document).on('click','a.deleteConfiguration', function() {
                fnOpenDeleteConfirmDialog($(this).attr('id'),"<spring:message code="TEXT_DELETE_TRANSLATION_CONFIRMATION"/> ","<c:url value="/service/deleteConfiguration"/>","<c:url value="/service"/>");
            });

        });
