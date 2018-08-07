package com.ya0ne.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.i18n.DatabaseMessageSource;
 
@Controller
public class LoginController {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(LoginController.class);
    @Autowired DatabaseMessageSource messageSource;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Locale locale ) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", messageSource.getMessage("TEXT_LOGIN_ERROR",null,null,locale));
        }

        model.setViewName("login");

        return model;
    }

	@RequestMapping(value="/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/logout", method = {RequestMethod.POST, RequestMethod.GET})
	public String logout(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
	    request.getSession().setAttribute("account",null);
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	        SecurityContextHolder.clearContext();
	    }
		return "redirect:/home";
	}
}
