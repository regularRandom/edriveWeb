package com.ya0ne.web.controller;

import static com.ya0ne.core.constants.WebConstants.WEB_MY;
import static com.ya0ne.core.constants.WebConstants.WEB_STATUS_ERROR;
import static com.ya0ne.core.constants.WebConstants.WEB_STATUS_OK;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_ERROR;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.ya0ne.core.exceptions.ServiceException;
import com.ya0ne.core.i18n.DatabaseMessageSource;
import com.ya0ne.web.service.MyService;

@Controller
@Secured({"ROLE_USER","ROLE_ADMIN"})
@RequestMapping(WEB_MY)
public class MyController {
    private static Logger logger = Logger.getLogger(MyController.class);
    private Gson gson = new Gson();
    private ModelAndView mv;

    @Autowired private MyService myService;
    @Autowired private DatabaseMessageSource messageSource;

    /**
     * Start page of my account
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(value="/account",
                    method = RequestMethod.GET)
    public ModelAndView showMy( ModelMap model, Locale locale ) {
        return myService.getMyAccount( locale, new ModelAndView() );
    }

    /**
     * Changes password of an account
     * @param newPassword
     * @return OK|NO_CONTENT
     */
    @RequestMapping(value="/changePassword",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> changePassword( @RequestParam(value = "newPassword", required = true ) String newPassword,
                                                                HttpServletRequest request ) {
        if( myService.changePassword( newPassword, request ) ) {
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_ERROR),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * 
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(value="/edit",
                    method = RequestMethod.GET)
    public ModelAndView showEditAccount( ModelMap model, Locale locale ) {
        return myService.getEditAccount( locale );
    }

    /**
     * Loads time zone data from the database, creates a tree with coordinates and uploads it to cloud
     * @param country
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value="/loadTimeZones",
                    method = RequestMethod.POST)
    public ModelAndView loadTimeZones( @RequestParam(value = "country", required = true ) String country, Locale locale ) {
        logger.info("Countries to load: " + country);
        mv = new ModelAndView("redirect:" + WEB_MY + "/account");
        try {
            myService.loadTimeZones(country);
            return myService.getMyAccount( locale, mv );
        } catch( ServiceException e ) {
            mv.addObject("timeZoneLoadError", messageSource.getMessage("TEXT_LOAD_GEO_TZ_DATA_ERROR",null,null,locale));
            return mv;
        }
    }

    /**
     * This method creates a map of countries with their boundaries and serialises it to the cloud
     * @param countriesBoundariesFile
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value = "/countries/serializeCurrentCountry",
                    method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView serializeCurrentCountry( long currentCountry, Locale locale ) {
        ModelAndView mv = new ModelAndView("redirect:" + WEB_MY + "/account");
        if( myService.serializeCurrentCountry(currentCountry).equals(WEB_UPLOAD_ERROR) ) {
            mv.addObject("uploadError3", messageSource.getMessage("TEXT_UPLOAD_TO_CLOUD_ERROR",null,null,locale));
        }
        return mv;
    }

    /**
     * This method returns recycle bin where entities are ready to be deleted. They can be restored.
     * @return ModelAndView
     */
    @RequestMapping(value = "/recyclebin",
                    method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView gerRecycleBin() {
        ModelAndView mv = new ModelAndView();
        return mv;
    }
}
