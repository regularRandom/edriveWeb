package com.ya0ne.web.controller;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.constants.WebConstants;
import com.ya0ne.web.service.HomeService;

@Controller
@RequestMapping(WebConstants.WEB_HOME)
public class HomeController {
    @Autowired private HomeService homeService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showHome( ModelMap model, Principal principal, Locale locale ) {
        return homeService.showModel(locale);
    }

    @RequestMapping(value="/about", method = RequestMethod.GET)
    public String showAbout() {
        return "about";
    }

}
