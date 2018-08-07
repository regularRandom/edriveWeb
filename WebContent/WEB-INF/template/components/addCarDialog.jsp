<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

        clearSelects();

        function clearSelects() {
            $("#carModels").hide();
            $("#carGenerations").hide();
            $("#carYearOfProductionsAdd").hide();
            clearDate();
            clearMileage();
        }

        function clearDate() {
            $('#carDateOfPurchaseAdd').datepicker('option','yearRange',$("#carYearOfProductionAdd option:selected").val() + ":+0");
            $('#carDateOfPurchaseAdd').datepicker('setDate',null);
        }

        function clearMileage() {
            $("#carInitialMileage").val(null);
            $("#carVinCode").val(null);
        }

        function clearYearsAndDate() {
            $("#carYearOfProductionsAdd").hide();
        }

        function addCarDialog() {
            $("#addCarDialog").dialog({
                resizable: false,
                modal: true,
                title: "<spring:message code="TEXT_ADD_CAR"/>",
                height: 550,
                width: 700,
                buttons: {
                    "<spring:message code="TEXT_BUTTON_CANCEL"/>": function () {
                        callbackCancel();
                    },
                    "<spring:message code="TEXT_SAVE"/>": function () {
                        saveMyNewCar();
                    }
                }
            });

            getCarsList();
            $("#addCarErrorField").empty();
            $("#addCarDialog").show();
        }    

        $('#carMake').change( function() {
            clearSelects();
            var id = $('#carMake option:selected').val();
            if( id > -1 ) {
                populateModels(makes[id]);
                myNewCarDto.makeId = makes[id].id;
            }
        });

        $('#carModel').change( function() {
            var id = $('#carModel option:selected').val();
            populateGenerations(models[id]);
            myNewCarDto.modelId = models[id].id;
            clearYearsAndDate();
            clearDate();
            clearMileage();
        });

        $('#carGeneration').change( function() {
            var id = $('#carGeneration option:selected').val();
            populateYearOfProduction(generations[id],'Add');
            myNewCarDto.generationId = generations[id].id;
            clearDate();
        });

        $('#carYearOfProductionAdd').change( function() {
            var id = $('#carYearOfProductionAdd option:selected').val();
            clearDate();
        });

        function populateModels( make ) {
            $('#carModel').find('option')
                .remove()
                .end()
                .append('<option><spring:message code="TEXT_ACCOUNT_ADD_CAR_MODEL"/></option>');
            var option = '';
            models = make.model;
            for( var i=0; i<models.length; i++ ) {
                option += '<option value="'+ i + '">' + models[i].name + '</option>';
            }
            $('#carModel').append(option);
            $('#carModels').show();
        }

        function populateGenerations( model ) {
            $('#carGeneration').find('option')
                .remove()
                .end()
                .append('<option><spring:message code="TEXT_ACCOUNT_ADD_CAR_MODEL_GENERATION"/></option>');
            var option = '';
            generations = model.generation.sort(function(a,b){ return b.startYear>a.startYear });
            for( var i=0; i<generations.length; i++ ) {
                option += '<option value="'+ i + '">' +
                        generations[i].startYear + 
                        ((generations[i].endYear == undefined) ? '' : ' - ' + generations[i].endYear) +
                        ((generations[i].name == undefined) ? '' : ', ' + generations[i].name) + '</option>';
            }
            $('#carGeneration').append(option);
            $('#carGenerations').show();
        }

        function callbackCancel() {
            $("#carMake").val($("#carMake option:first").val());
            clearSelects();
            $("#addCarDialog").dialog("close");
            return false;
        }
