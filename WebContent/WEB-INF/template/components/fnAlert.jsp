<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        function fnOpenAlert(text) {
            $("#alertBox").html(text);
            $("#alertBox").dialog({
                resizable: false,
                modal: true,
                title: "Alert",
                height: 200,
                width: 400,
                buttons: {
                    "<spring:message code="TEXT_OK"/>": function () {
                        $(this).dialog('close');
                    }
                }
            });
        }
