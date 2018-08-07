package com.ya0ne.web.service;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.exceptions.CarException;
import com.ya0ne.core.generated.MyNewCarDto;

public interface CarService {

    /**
     * Shows model and view for the add/edit cars
     * @return ModelAndView
     */
    ModelAndView showModel( Locale locale );
    
    /**
     * Returns list of cars of account in format of MyNewCarDto[]
     * @param accountId
     * @return JSON string
     */
    String getMyCarsList();

    /**
     * Adds car to customer
     * @throws CarException
     */
    MyNewCarDto addCar( String myNewCar, Locale locale ) throws CarException;

    /**
     * Adds car to customer
     * @throws CarException
     */
    Long editCar( String myCar ) throws CarException;

    /**
     * Returns current position of the car 
     * @param accountId
     * @return TrackPoint
     */
    String getCurrentPosition( long accountId, Locale locale );

    /**
     * Returns list of all cars makes
     * @param locale
     * @return JSON string
     */
    String getCarsList( Locale locale );
}
