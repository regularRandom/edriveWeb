package com.ya0ne.web.service;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.exceptions.ServiceException;

public interface MyService {

    /**
     * Returns ModelAndView with all account's data: parameters, cars, services etc.
     * @param locale
     * @return ModelAndView
     */
    ModelAndView getMyAccount( Locale locale, ModelAndView mv );
    
    /**
     * Changes the account's password
     * @param newPassword
     * @return true|false
     */
    boolean changePassword( String newPassword, HttpServletRequest request );

    /**
     * Returns ModelAndView ready for edit 
     * @param locale
     * @return ModelAndView
     */
    ModelAndView getEditAccount( Locale locale );

    /**
     * Loads timezones data for country. In case of country is null, then returns whole set.  
     * @param country
     * @return Car and array of countries names
     */
    Object[] loadTimeZones( String country ) throws ServiceException;

    /**
     * Serialises current country to cloud
     * @param countryId
     * @return
     */
    String serializeCurrentCountry( long countryId );
}
