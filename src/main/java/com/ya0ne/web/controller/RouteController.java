package com.ya0ne.web.controller;

import static com.ya0ne.core.constants.Constants.ERROR;
import static com.ya0ne.core.constants.WebConstants.WEB_ROUTE;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ya0ne.core.exceptions.RouteException;
import com.ya0ne.web.service.RouteService;

@Controller
@RequestMapping(WEB_ROUTE)
public class RouteController {

    @Autowired private RouteService routeService;

    @RequestMapping(value="/calculateDirectrions",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> calculateDirectrions( @RequestParam(value = "q", required = true ) String q ) {
        try {
            return new ResponseEntity<String>(routeService.calculateRouteDirections( q ),HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/findLocation", 
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> findLocation( @RequestParam(value = "q", required = true ) String q, Locale locale ) {
        try {
            return new ResponseEntity<String>(routeService.findLocation(q, locale), HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<String>(ERROR,HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
