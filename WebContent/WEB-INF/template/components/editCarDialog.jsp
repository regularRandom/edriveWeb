<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        function editCarDialog( id ) {
            $("#editCarErrorField").empty();
            $("#editCarDialog").show();

            $("#editCarDialog").dialog({
                resizable: false,
                modal: true,
                title: "<spring:message code="TEXT_ADD_CAR"/>",
                height: 400,
                width: 600,
                buttons: {
                    "<spring:message code="TEXT_BUTTON_CANCEL"/>": function () {
                        $(this).dialog("close");
                    },
                    "<spring:message code="TEXT_SAVE"/>": function () {
                        saveMyCarForUpdate(id);
                    }
                }
            });

            $.when(getCarsList())
                .then(function() {getMyCars(id);});
        }    

        function saveEditCar() {
            return false;
        }
