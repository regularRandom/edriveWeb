package com.ya0ne.web.service;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

public interface HomeService {

    /**
     * Shows model and view for the add/edit cars
     * @return ModelAndView
     */
    ModelAndView showModel( Locale locale );
}
