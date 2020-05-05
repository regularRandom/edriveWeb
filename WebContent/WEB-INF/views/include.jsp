<%@ page trimDirectiveWhitespaces="true" %>
<%@ page session="true"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<c:set var="compressOnOff" scope="application">
    <spring:eval expression="@propertyConfigurer.getProperty('code.compress')"/>
</c:set>
<c:set var="googleApiKey" scope="application">
    <spring:eval expression="@propertyConfigurer.getProperty('googleApiKey')"/>
</c:set>
