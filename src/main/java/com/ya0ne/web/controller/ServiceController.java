package com.ya0ne.web.controller;

import static com.ya0ne.core.constants.Constants.ERROR;
import static com.ya0ne.core.constants.Constants.OK;
import static com.ya0ne.core.constants.WebConstants.WEB_SERVICE;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.exceptions.CSVUploadException;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.exceptions.ConfigurationException;
import com.ya0ne.core.exceptions.TranslationException;
import com.ya0ne.core.i18n.DatabaseMessageSource;
import com.ya0ne.web.service.SystemService;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping(WEB_SERVICE)
public class ServiceController {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(TracksController.class);

    @Autowired private DatabaseMessageSource messageSource;
    @Autowired private SystemService service;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showService( Locale locale ) {
        return service.getService( locale );
    }

    /**
     * Uploads file with geodata - coordinates with time zones
     * @param geoTZFile
     * @param locale
     * @return
     */
    @RequestMapping(value="/uploadGeoTimeZoneData",
                    method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ModelAndView> uploadGeoTimeZoneData( @RequestParam("geoTZFile") MultipartFile geoTZFile, 
                                                               Locale locale ) {
        DeferredResult<ModelAndView> result = new DeferredResult<>();
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        if( !geoTZFile.isEmpty() ) {
            try {
                service.uploadGeoTZCSV( geoTZFile );
            } catch (CSVUploadException e) {
                mv.addObject("uploadErrorGeoCSV", messageSource.getMessage("TEXT_UPLOAD_WRONG_GEO_CSV_FILE",null,null,locale));
                e.printStackTrace();
            }
        } else {
            mv.addObject("uploadErrorGeoCSV", messageSource.getMessage("TEXT_UPLOAD_GEO_CSV_EMPTY",null,null,locale));
        }
        result.setResult(mv);
        return result;
    }

    /**
     * Uploads file with admin codes (regions) - admin1codes.txt
     * @param geoTZFile
     * @param locale
     * @return
     */
    @RequestMapping(value="/uploadDistricts",
                    method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ModelAndView> uploadDistricts( @RequestParam("districtFile") MultipartFile districtFile, 
                                                         Locale locale ) {
        DeferredResult<ModelAndView> result = new DeferredResult<>();
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        if( !districtFile.isEmpty() ) {
            try {
                service.uploadDistrictsCSV( districtFile );
            } catch( CSVUploadException e ) {
                mv.addObject("uploadErrorDistricts", messageSource.getMessage("TEXT_UPLOAD_WRONG_GEO_CSV_FILE",null,null,locale));
            }
        } else {
            mv.addObject("uploadErrorDistricts", messageSource.getMessage("TEXT_UPLOAD_BACKLOG_EMPTY",null,null,locale));
        }
        result.setResult(mv);
        return result;
    }

    /**
     * Uploads file with admin codes (regions) - admin2codes.txt
     * @param geoTZFile
     * @param locale
     * @return
     */
    @RequestMapping(value="/uploadDistricts2",
                    method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ModelAndView> uploadDistricts2( @RequestParam("districtFile2") MultipartFile districtFile, 
                                                          Locale locale ) {
        DeferredResult<ModelAndView> result = new DeferredResult<>();
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        try {
            service.uploadDistrictsCSV2( districtFile );
        } catch( CSVUploadException e ) {
            mv.addObject("uploadErrorDistricts2", messageSource.getMessage("TEXT_UPLOAD_WRONG_GEO_CSV_FILE",null,null,locale));
        }
        result.setResult(mv);
        return result;
    }

    /**
     * Gets list of translations
     * @param locale
     * @return JSON list
     */
    @RequestMapping(value="/getTranslations",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getTranslations( Locale locale ) {
        return new ResponseEntity<String>(service.getTranslations(),HttpStatus.OK);
    }

    /**
     * Gets list of translations
     * @param locale
     * @return JSON list
     */
    @RequestMapping(value="/getTranslation",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getTranslation( @RequestParam(value = "editId", required = true ) String dataIds,
                                                   Locale locale ) {
        return new ResponseEntity<String>(service.getTranslation( dataIds ),HttpStatus.OK);
    }

    /**
     * Edits translation
     * @param translationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/editTranslation/{id}",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> editTranslation( @PathVariable("id") Long translationId,
                                                    Locale locale ) {
        return new ResponseEntity<String>(service.getTranslations(),HttpStatus.OK);
    }

    /**
     * Deletes translation
     * @param translationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/deleteTranslation",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteTranslation( @RequestParam(value = "dataIds", required = true ) String dataIds,
                                                      Locale locale ) {
        try {
            service.deleteTranslation( dataIds );
        } catch( TranslationException e ) {
            return new ResponseEntity<String>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(OK, HttpStatus.OK);
    }

    /**
     * Edits translation
     * @param translationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/saveTranslation",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveTranslation( @RequestParam("translation") String translation,
                                                   @RequestParam("update") String dataIds,
                                                   Locale locale ) {
        try {
            if( dataIds.equals("new") ) {
                service.saveTranslation( translation );
            } else {
                service.updateTranslation( translation, dataIds );
            }
        } catch( TranslationException e ) {
            return new ResponseEntity<String>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(OK, HttpStatus.OK);
    }

    /**
     * Reloads database message source
     * @param locale
     */
    @RequestMapping(value="/reloadTranslations",
                    method = RequestMethod.GET)
    public ModelAndView reloadTranslations( Locale locale ) {
        service.reloadTranslations();
        ModelAndView mv = service.getService( locale );
        mv.setViewName("redirect:" + WEB_SERVICE);
        return mv;
    }

/**
 * Configuration
 */

    /**
     * Gets list of configuration records
     * @param locale
     * @return JSON list
     */
    @RequestMapping(value="/getConfigurations",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getConfigurations() {
        return new ResponseEntity<String>(service.getConfigurations(),HttpStatus.OK);
    }

    /**
     * Gets configuration entity for edit
     * @param locale
     * @return JSON list
     */
    @RequestMapping(value="/getConfiguration",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> getConfiguration( @RequestParam(value = "editId", required = true ) String key,
                                                    Locale locale ) {
        return new ResponseEntity<String>(service.getConfiguration( key ),HttpStatus.OK);
    }

    /**
     * Edits configuration entity
     * @param configurationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/editConfiguration/{id}",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> editConfiguration( @PathVariable("id") Long translationId,
                                                     Locale locale ) {
        return new ResponseEntity<String>(service.getConfigurations(),HttpStatus.OK);
    }

    /**
     * Deletes configuration entity
     * @param configurationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/deleteConfiguration",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteConfiguration( @RequestParam(value = "deleteId", required = true ) String key,
                                                       Locale locale ) {
        try {
            service.deleteConfiguration( key );
        } catch( ConfigurationException e ) {
            return new ResponseEntity<String>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(OK, HttpStatus.OK);
    }

    /**
     * Edits configuration
     * @param configurationId
     * @param locale
     * @return reload page
     */
    @RequestMapping(value="/saveConfiguration",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveConfiguration( @RequestParam("configuration") String configuration,
                                                     @RequestParam("update") String key,
                                                     Locale locale ) {
        try {
            if( key.equals("new") ) {
                service.saveConfiguration( configuration );
            } else {
                service.updateConfiguration( configuration, key );
            }
        } catch( ConfigurationException e ) {
            return new ResponseEntity<String>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(OK, HttpStatus.OK);
    }

    /**
     * Reloads configuration
     * @param locale
     */
    @RequestMapping(value="/reloadConfiguration",
                    method = RequestMethod.GET)
    public ModelAndView reloadConfiguration( Locale locale ) {
        service.reloadConfigurations();
        ModelAndView mv = service.getService( locale );
        mv.setViewName("redirect:" + WEB_SERVICE);
        return mv;
    }

    /**
     * This method creates a map of countries with their boundaries and serialises it to the cloud
     * @param countriesBoundariesFile
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value = "/loadBoundaries",
                    method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView loadBoundaries( @RequestParam("countriesBoundariesFile") MultipartFile countriesBoundariesFile,
                                        Locale locale ) {
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        if( !countriesBoundariesFile.isEmpty() ) {
            try {
                service.loadCountriesBoundaries(countriesBoundariesFile);
            } catch( CSVUploadException e ) {
                mv.addObject("uploadErrorBoundaries", messageSource.getMessage("TEXT_UPLOAD_WRONG_COUNTRIES_CSV_FILE",null,null,locale));
            }
        } else {
            mv.addObject("uploadErrorBoundaries", messageSource.getMessage("TEXT_UPLOAD_COUNTRIES_BOUNDARIES_EMPTY",null,null,locale));
        }
        return mv;
    }

    /**
     * This method creates a map of countries with their boundaries and serialises it to the cloud
     * @param countriesBoundariesFile
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value = "/refreshCountries",
                    method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView refreshCountries( Locale locale ) {
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        try {
            service.refreshCountries();
        } catch (CloudException e) {
            mv.addObject("uploadErrorRefreshCountries", messageSource.getMessage("TEXT_REFRESH_COUNTRIES_IN_CLOUD_ERROR",null,null,locale));
        }
        return mv;
    }

    /**
     * This method creates a map of time zones and serializes it to the cloud
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(value = "/loadTimeZones",
                    method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView refreshTimeZones( Locale locale ) {
        ModelAndView mv = new ModelAndView("redirect:" + WEB_SERVICE);
        try {
            service.loadTimeZones();
        } catch (CloudException e) {
            mv.addObject("uploadErrorRefreshTimeZones", messageSource.getMessage("TEXT_REFRESH_TIMEZONES_IN_CLOUD_ERROR",null,null,locale));
        }
        return mv;
    }
}
