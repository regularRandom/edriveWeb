package com.ya0ne.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import static com.ya0ne.core.constants.WebConstants.WEB_MYCARS;
import static com.ya0ne.core.constants.WebConstants.WEB_STATUS_ERROR;
import static com.ya0ne.core.constants.WebConstants.WEB_STATUS_OK;

import java.util.Locale;

import com.google.gson.Gson;
import com.ya0ne.core.exceptions.CarException;
import com.ya0ne.web.service.CarService;

@Controller
@Secured({"ROLE_USER","ROLE_ADMIN"})
@RequestMapping(WEB_MYCARS)
public class CarsController {
    @Autowired private CarService carService;
    private Gson gson = new Gson();

    @RequestMapping(value="/model",
                    method = RequestMethod.GET)
    public ModelAndView showModel( Locale locale ) {
        return carService.showModel( locale );
    }

    @RequestMapping(value="/addCar", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> addCar( @RequestParam(value = "myNewCar", required = true ) String myNewCar,
                                          Locale locale ) {
        try {
            carService.addCar( myNewCar, locale );
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK); 
        } catch( CarException e ) {
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_ERROR),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/editCar", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> editCar( @RequestParam(value = "myCar", required = true ) String myCar ) {
    try {
        carService.editCar( myCar );
        return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK); 
    } catch( CarException e ) {
        return new ResponseEntity<String>(gson.toJson(WEB_STATUS_ERROR),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    }

    @RequestMapping(value="/getCurrentPosition", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCurrentPosition( @RequestParam(value = "accountId", required = true ) long accountId,
                                                      Locale locale ) {
        return new ResponseEntity<String>(carService.getCurrentPosition(accountId,locale),HttpStatus.OK); 
    }

    @RequestMapping(value="/getCarsList", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCarsList( Locale locale ) {
        return new ResponseEntity<String>(carService.getCarsList(locale),HttpStatus.OK); 
    }

    /**
     * This method creates a map of countries with their boundaries and serialises it to the cloud
     * @param countriesBoundariesFile
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value = "/getMyCars",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getMyCars() {
        return new ResponseEntity<String>(carService.getMyCarsList(),HttpStatus.OK);
    }
}
