<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        function updatePasswordDialog() {
            $("#changePasswordErrorField").empty();
            $("#updatePasswordDialog").show();

            $("#updatePasswordDialog").dialog({
                resizable: false,
                modal: true,
                title: "<spring:message code="TEXT_CHANGE_PASSWORD"/>",
                height: 350,
                width: 400,
                buttons: {
                    "<spring:message code="TEXT_SAVE"/>": function () {
                        callback($($("#changePasswordForm input[name=currentPassword]")).val(),$($("#changePasswordForm input[name=newPassword]")).val());
                    }
                }
            });
        }    

        function callback( oldP, newP ) {
            if( oldP == '' || newP == '' ) {
                $("#changePasswordErrorField").html('<spring:message code="TEXT_PASSWORDS_CANNOT_BE_EMPTY"/>');
                return false;
            }
            if( "${account.getPassword()}" == $.md5(newP) ) {
                $("#changePasswordErrorField").html('<spring:message code="TEXT_PASSWORDS_CANNOT_BE_THE_SAME"/>');
                return false;
            }
            if( "${account.getPassword()}" == $.md5(oldP) ) {
                $.post( '<c:url value="/my/changePassword"/>', { newPassword : $.md5(newP) }, function(data) {
                        if( $.trim(data) == '' ) {
                            alert("<spring:message code="TEXT_CHANGE_PASSWORD_ERROR"/>");
                        }
                    },
                    'json' // I expect a JSON response
                );
                location.reload();
            } else {
                $("#changePasswordErrorField").html('<spring:message code="TEXT_CURRENT_PASSWORD_DO_NOT_MATCH"/>');
                return false;
            }
            
        }
