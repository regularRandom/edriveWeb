<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        function showHideHolders(what,show,hide) {
            $(what).click(function() {
                //get collapse content selector
                var collapse_content_selector = $(this).attr('href');
                //make the collapse content to be shown or hide
                var toggle_switch = $(this);
                $(collapse_content_selector).toggle(function(){
                    if($(this).css('display')=='none'){
                        //change the button label to be 'Show'
                        toggle_switch.html(show);
                    } else {
                        //change the button label to be 'Hide'
                        toggle_switch.html(hide);
                    }
                });
            });
        }
