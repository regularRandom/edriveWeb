package com.ya0ne.web.service;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

public interface SettingsService {
    /**
     * Fethes all settings of customers and shows them in a grid
     * @param pageSize
     * @return
     */
    ModelAndView getSettings( Locale locale );
}
