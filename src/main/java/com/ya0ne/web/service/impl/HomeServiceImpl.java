package com.ya0ne.web.service.impl;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.domain.Parameter;
import com.ya0ne.core.domain.dao.car.CarDAO;
import com.ya0ne.core.factories.ConfigurationFactory;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.factories.ParameterFactory;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.web.service.HomeService;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired private CarDAO carDao;
    @Autowired private DomainUtilities domainUtilities;
    @Autowired private ParameterFactory parameters;
    @Autowired private LanguageFactory languages;
    @Autowired private ConfigurationFactory configuration;

    @Override
    public ModelAndView showModel(Locale locale) {
        ModelAndView mv = new ModelAndView("home");
        Map<String, Parameter> parametersMap = parameters.getParameters().get(languages.getLanguage(locale.getLanguage()));
        mv.addObject("myCars",carDao.getMyCars(domainUtilities.getCustomerId()));
        mv.addObject("parameters", parametersMap);
        mv.addObject("configuration", configuration.getConfiguration());
        return mv;
    }
}
