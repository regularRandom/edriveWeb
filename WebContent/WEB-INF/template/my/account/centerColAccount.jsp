<%@ include file="/WEB-INF/views/include.jsp" %>

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?v=3&key=${googleApiKey}"></script>
<script src="<c:url value="/resources/js/jquery.md5.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/css/pqselect.css"/>" />
<script src = "<c:url value="/resources/js/pqselect.js"/>"></script>
<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
    var strings = new Array();
    strings['TEXT_ACCOUNT_ADD_CAR'] = "<spring:message code='TEXT_ACCOUNT_ADD_CAR' javaScriptEscape='true' />";
    strings['TEXT_ALL_FIELDS_MUST_BE_FILLED'] = "<spring:message code='TEXT_ALL_FIELDS_MUST_BE_FILLED'/>";

    var bounds = new google.maps.LatLngBounds();
    var flightPlanCoordinates = [];
</compress:js>
</script>
<script src="<c:url value="/resources/js/my/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/js/my/functions.js"/>" type="text/javascript"></script>
<script src="<c:url value="/resources/js/my/account.js"/>" type="text/javascript"></script>

<script type="text/javascript">
<compress:js enabled="${compressOnOff}" jsCompressor="closure">
/*<![CDATA[*/
    $(document).ready(function() {
        $("#updatePasswordDialog").hide();
        $("#addCarDialog").hide();
        $("#editCarDialog").hide();
        
        $(document).on('click','a.changePassword', function() {
            updatePasswordDialog();
        });

        $(document).on('click','a.addCar', function() {
            addCarDialog();
        });

        $(document).on('click','a.editCar', function() {
            editCarDialog($(this).attr('id'));
        });

        <tiles:insertAttribute name="addCarDialog"/>
        <tiles:insertAttribute name="editCarDialog"/>
        <tiles:insertAttribute name="updatePasswordDialog"/>

        $(function() {
            //initialize the pqSelect widget.
            $("#select3").pqSelect({
                multiplePlaceholder: '<spring:message code="TEXT_SELECT_COUNTRIES_TO_LOAD"/>',
                checkbox: true //adds checkbox to options
            }).on("change", function(evt) {
                var val = $(this).val();
            }).pqSelect('close');

            $("#select0").pqSelect({
                singlePlaceholder: '<spring:message code="TEXT_SELECT_CURRENT_COUNTRY"/>',
                radio: false //adds radio buttons
            }).on("change", function(evt){
                var val = $(this).val();
            }).pqSelect( 'close' );
        });

        $('#submitLoadGeoTZ').click( function() {
            if( $('#select3 :selected').length > 0 ) {
                $('#submitLoadGeoTZ').submit();
                return true;
            } else {
                alert("<spring:message code="TEXT_LOAD_GEO_TZ_DATA_EMPTY_LIST_OF_COUNTRIES"/>");
                return false;
            }
        });
    });
/*]]>*/
</compress:js>
</script>
  <div id="mapCarLocationInFullScreen" title="map"></div>
  <div id="colTwoCenter">
  <div id="updatePasswordDialog">
  <span class="changePasswordError" id="changePasswordErrorField"></span><br/>
  <form action="" method="post" id="changePasswordForm">
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_CURRENT_PASSWORD"/>
    <input size="34" maxlength="32" value="" name="currentPassword" id="currentPassword" type="password"/></p>
    </div>
  </p>
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_NEW_PASSWORD"/>
    <input size="34" maxlength="32" value="" name="newPassword" id="newPassword" type="password" /></p>
    </div>
  </p>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>

  <div id="addCarDialog">
  <span class="addCarError" id="addCarErrorField"></span><br/>
  <form action="" method="post" id="addCarForm">
  <p>
    <div style="text-align: left;">
        <p style="line-height: 30px;"><spring:message code="TEXT_ACCOUNT_ADD_CAR"/>&nbsp;
        <select id="carMake">
            <option selected><spring:message code="TEXT_ACCOUNT_ADD_CAR"/></option>
        </select>
        </p>
    </div>
    <div style="text-align: left;" id="carModels">
        <p style="line-height: 30px;"><spring:message code="TEXT_ACCOUNT_ADD_CAR_MODEL"/>&nbsp;
        <select id="carModel"></select>
        </p>
    </div>
    <div style="text-align: left;" id="carGenerations">
        <p style="line-height: 30px;"><spring:message code="TEXT_ACCOUNT_ADD_CAR_MODEL_GENERATION"/>&nbsp;
        <select id="carGeneration"></select>
        </p>
    </div>
    <div style="text-align: left;" id="carYearOfProductionsAdd">
        <p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_YEAR_OF_PRODUCTION"/>&nbsp;
        <select id="carYearOfProductionAdd"></select>
        </p>
        <p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_DATE_OF_PURCHASE"/>&nbsp;
        <input id="carDateOfPurchaseAdd"/>
        </p>
        <p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_INITIAL_MILEAGE"/>&nbsp;
        <input id="carInitialMileage"/>
        </p>
        <p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_VIN"/>&nbsp;
        <input id="carVinCode" maxlength="17"/>
        </p>
    </div>
  </p>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>

  <div id="editCarDialog">
  <span class="editCarError" id="editCarErrorField"></span><br/>
  <form action="" method="post" id="editCarForm">
  <p>
    <div style="text-align: left;"><p style="line-height: 30px;"><spring:message code="TEXT_ACCOUNT_ADD_CAR"/>:&nbsp;<span id="carEditName"></span>
    </div>
    <div style="text-align: left;" id="carYearOfProductionsEdit"><p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_YEAR_OF_PRODUCTION"/>&nbsp;
    <select id="carYearOfProductionEdit"></select>
    </p>
    <p style="line-height: 30px;"><spring:message code="TEXT_C_PROP_ACCOUNT_DATE_OF_PURCHASE"/>&nbsp;
    <input id="carDateOfPurchaseEdit"/>
    </p>
    <p style="line-height: 30px;"><spring:message code="TEXT_CURRENT_MILEAGE"/>&nbsp;
    <input id="carCurrentMileage"/>
    </p>
    </div>
  </p>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
  </div>

  <table width="95%" cellpadding="5" border="0">
    <tr>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_CARS"/></h3>&nbsp;<a href="#" class="addCar"><spring:message code="TEXT_ADD_CAR"/></a></p>
        <table width="70%">
        <c:forEach var="myCar" items="${myCars}">
            <tr>
                <td><input type="checkbox">&nbsp;<a href="<c:url value="/cars/${myCar.generationId}"/>"><c:out value="${myCar.name}"/></a> - <a href="#" class="carLocation" id="${myCar.accountId}"></a></td>
                <td align="right"><a href="#" id="${myCar.accountId}" class="editCar"/><img src="<c:url value="/resources/images/button-edit2.png"/>" border="0"/></a>&nbsp;<a href="#" class="updateMileage"><img src="<c:url value="/resources/images/Wheel-icon.png"/>" border="0"/></a></td>
            </tr>
        </c:forEach>
        </table>
        <p><h3><spring:message code="TEXT_SYSTEM_CURRENT_LOCATION"/></h3></p>
        <form action="<c:url value="/my/countries/serializeCurrentCountry"/>" method="POST">
        <table width="100%">
        <tr>
            <td>
            <select id="select0" name="currentCountry" style="margin: 20px;width:300px;">
            <option></option>
            <c:forEach var="country" items="${countries}">
                <option value="${country.id}">${country.iso2} - ${country.nm}</option>
            </c:forEach>
            </select>&nbsp;<input type="submit" value="<spring:message code="TEXT_REFRESH"/>"/>
            </td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>

        </td>
        <td width="50%" style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_CUSTOM_PARAMETERS"/></h3>&nbsp;<a href="<c:url value="/my/editParameters"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        <table width="100%" border="0">
        <c:forEach var="myParameter" items="${myParameters}">
            <tr>
                <c:set var="metaData" value="${myParameter.getValue().getMetaData()}"/>
                <td align="left" width="60%"><c:out value="${metaData.values().toArray()[0].name}"/>:</td>
                <c:set var="parameter" value="${myParameter.getValue()}"/> <!-- get parameter from Map<String,Parameter> -->
                <td><c:out value="${parameter.getValue().getValue()}"/></td> <!-- get parameter value from Parameter -->
            </tr>
        </c:forEach>
        </table>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_ACCOUNT_DATA"/> - ${myAccountData.getName()} (${myAccountData.getType().getValue()})</h3>&nbsp;<a href="<c:url value="/my/edit"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        <table width="100%">
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_CITY"/>:</td>
                <td width="50%">${myAccountData.getAccountProfile().getAddress()}</td>
            </tr>
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_EMAIL"/>:</td>
                <td width="50%">${myAccountData.getEmail()}</td>
            </tr>
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_DEFAULT_CURRENCY"/>:</td>
                <td width="50%">${myAccountData.getCurrency().getCode()}</td>
            </tr>
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_EMAIL_FORMAT"/>:</td>
                <td width="50%">${myAccountData.getAccountProfile().getEmailFormat()}</td>
            </tr>
            <tr>
                <td width="50%"><spring:message code="TEXT_ACCOUNT_PASSWORD"/>:</td>
                <td width="50%"><a href="#" class="changePassword"><spring:message code="TEXT_CHANGE_PASSWORD"/></a></td>
            </tr>
            <tr>
            </tr>
        </table>
        </td>
        <td style="vertical-align: top">
        <p><h3><spring:message code="TEXT_MY_SERVICES"/></h3>&nbsp;<a href="<c:url value="/my/editServices"/>"><spring:message code="TEXT_EDIT_ACCOUNT"/></a></p>
        <p><h3><spring:message code="TEXT_MY_SERVICE_DATA"/></h3></p>
        <form action="<c:url value="/my/loadTimeZones"/>" method="POST" id="loadGeoTZData">
        <table width="100%">

        <c:if test="${not empty param.timeZoneLoadError}">
        <tr>
            <td style="text-align: center;">
            <div class="uploadError">
                <p><label>${param.timeZoneLoadError}</label></p>
            </div>
            </td>
        </tr>
        </c:if>

        <tr>
            <td>
            <select id="select3" multiple=multiple name="country" style="margin: 20px;width:300px;">
            <option></option>
            <c:forEach var="country" items="${countriesWithTimeZones}">
                <option value="${country.id}">${country.iso2} - ${country.nm}</option>
            </c:forEach>
            </select>&nbsp;<input type="submit" value="<spring:message code="TEXT_LOAD_GEO_TZ_DATA"/>" id="submitLoadGeoTZ"/>
            </td>
        </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
        </td>
    </tr>
  </table>
  </div>
