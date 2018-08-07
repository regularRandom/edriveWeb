package com.ya0ne.web.service;

import java.util.Locale;

import com.ya0ne.core.exceptions.RouteException;

public interface RouteService {

    /**
     * Calls OSRM backend to get route's polyline
     * @return JSON
     */
    String calculateRouteDirections( String jsonString ) throws RouteException;

    /**
     * Returns JSON representation of location (LocalSearchAjax
     * @param place (city name)
     * @return JSON string
     */
    String findLocation( String place, Locale locale ) throws RouteException;
}
