package com.ya0ne.web.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.constants.WebConstants;
import com.ya0ne.web.service.SettingsService;

@Controller
@Secured({"ROLE_USER","ROLE_ADMIN"})
@RequestMapping(WebConstants.WEB_MY)
public class SettingsController {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SettingsController.class);
    
    @Autowired
    private SettingsService settingsService;
    
    @RequestMapping(value=WebConstants.WEB_MYACCOUNT, method = RequestMethod.GET)
    public ModelAndView showSettings( Locale locale ) {
        return settingsService.getSettings( locale );
    }
}
