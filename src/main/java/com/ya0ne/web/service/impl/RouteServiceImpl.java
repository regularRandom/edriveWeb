package com.ya0ne.web.service.impl;

import static com.ya0ne.core.constants.Constants.OSRM_SERVER;
import static com.ya0ne.core.constants.WebConstants.OSRM_ROUTE;
import static com.ya0ne.core.domain.converters.LocationConverter.toOSRMResponseDto;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ya0ne.core.domain.converters.LocationConverter;
import com.ya0ne.core.domain.dao.RouteDAO;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.exceptions.RouteException;
import com.ya0ne.core.factories.ConfigurationFactory;
import com.ya0ne.core.generated.LocationSearchDto;
import com.ya0ne.core.utilities.HibernateProxyTypeAdapter;
import com.ya0ne.core.utilities.http.rest.OSRMUtilities;
import com.ya0ne.web.service.RouteService;

@Service
public class RouteServiceImpl implements RouteService {
    private static Logger logger = Logger.getLogger(RouteServiceImpl.class);

    @Autowired private RouteDAO routeDao;
    @Autowired private LocationConverter locationConverter;
    @Autowired private OSRMUtilities osrmUtilities;
    @Autowired private ConfigurationFactory configurationFactory;

    private Gson gson = new Gson();
    private GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

    @Override
    public String calculateRouteDirections( String json ) throws RouteException {
        logger.info("Input JSON string: " + json);
        final String OSRM_SERVER_URI = configurationFactory.getConfigurationValue(OSRM_SERVER) + OSRM_ROUTE;
        LocationSearchDto[] dtos = gson.fromJson(json, LocationSearchDto[].class);
        ResponseEntity<String> response = osrmUtilities.viaroute(OSRM_SERVER_URI, dtos);
        JSONObject jsonObj = null;
        if( response != null ) {
            jsonObj = (JSONObject)JSONValue.parse(response.getBody());
        }
        return gson.toJson(toOSRMResponseDto(jsonObj));
    }

    @Override
    public String findLocation(String place, Locale locale) throws RouteException {
        try {
            return gsonBuilder.create().toJson(locationConverter.toLocationSearchDto(routeDao.findLocation(place, locale),locale));
        } catch (DAOException e) {
            throw new RouteException(e.getMessage()); 
        }
    }
}
