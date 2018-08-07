<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        function insertUpdateConfigurationDialog( updateId ) {
            document.getElementById('configurationForm').reset();
            $("#configurationErrorField").empty();
            $("#insertUpdateConfigurationDialog").show();

            if( updateId !== 'new' ) {
                $.post( '<c:url value="${request.getRequestURL()}/service/getConfiguration"/>', { editId : updateId }, function(response) {
                        if( $.trim(response) == 'ERROR' ) {
                            $("#configurationErrorField").html('<spring:message code="TEXT_UPDATE_CONFIGURATION_ERROR"/>');
                        } else {
                            $("#configurationId").val(response.id);
                            $("#configurationKey").val(response.key);
                            $("#configurationValue").val(response.value);
                            $("#configurationDescription").val(response.description);
                        }
                    },
                    'json' // I expect a JSON response
                );
            }

            $("#insertUpdateConfigurationDialog").dialog({
                resizable: false,
                modal: true,
                title: "<spring:message code="TEXT_CONFIGURATION"/>",
                height: 420,
                width: 410,
                buttons: {
                    "<spring:message code="TEXT_SAVE"/>": function () {
                        var id = document.getElementsByClassName("configurationId")[0].value;
                        var configurationDto = {
                            id : id == "" ? "0" : id,
                            key : document.getElementsByClassName("configurationKey")[0].value,
                            value : document.getElementsByClassName("configurationValue")[0].value,
                            description : document.getElementsByClassName("configurationDescription")[0].value
                        };
                        insertUpdateConfigurationCallback(configurationDto,updateId);
                    }
                }
            });
        }    

        function insertUpdateConfigurationCallback( dto, updateId ) {
            if( dto.key !== '' && dto.value !== '' ) {
                $.post( '<c:url value="${request.getRequestURL()}/service/saveConfiguration"/>', { configuration : JSON.stringify(dto), update : updateId }, function(response) {
                        if( $.trim(response) == 'ERROR' ) {
                            $("#configurationErrorField").html('<spring:message code="TEXT_INSERT_CONFIGURATION_ERROR"/>');
                            return false;
                        }
                    },
                    'json' // I expect a JSON response
                );
            location.reload();
            } else {
                $("#configurationErrorField").html('<spring:message code="TEXT_FORM_CONFIGURATION_ERROR"/>');
            }
        }
