package com.ya0ne.web.service.impl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.web.service.SettingsService;

@Service
public class SettingsServiceImpl implements SettingsService {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SettingsServiceImpl.class);
    
    @Autowired private DomainUtilities domainUtilities;
    @Autowired private LanguageFactory languages;

    @Override
    public ModelAndView getSettings(Locale locale) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("parameters",domainUtilities.getAccount().getParameters().get(languages.getLanguage(locale.getLanguage())));
        return mv;
    }
    
}
