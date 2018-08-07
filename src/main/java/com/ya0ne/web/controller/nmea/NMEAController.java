package com.ya0ne.web.controller.nmea;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.ya0ne.core.constants.WSConstants;
import com.ya0ne.web.service.ws.NMEAService;

@RestController
@Secured("ROLE_CAR")
@RequestMapping(WSConstants.WS_NMEA)
public class NMEAController {
    private static Logger logger = Logger.getLogger(NMEAController.class);

    @Autowired ApplicationContext context;
    @Autowired HttpServletRequest request;
    @Autowired NMEAService service;
    
    private Gson gson = new Gson();

    @RequestMapping(value = WSConstants.WS_HEALTHCHECK, 
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> healtCheck() {
        logger.info("alive");
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * Method save a data to track with trackId
     * @param trackId - track ID received from the getTrack method
     * @param nmeaStr - JSON encoded coordinates
     * @return OK or ERROR
     */
    @RequestMapping(value = WSConstants.WS_SAVETRKPT, 
            method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            headers = "content-type=application/x-www-form-urlencoded")
    public @ResponseBody ResponseEntity<String> saveNmeaSentence(@RequestParam String nmeaStr) {
        logger.info("Message from the source: " + nmeaStr);
        if( service.processNmeaSentence(nmeaStr) != WSConstants.WS_OK ) {
            return new ResponseEntity<String>("ERROR", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * Method creates a new track and returns its ID
     * @param car - encrypted in ASE VIN code
     * @param hashCode - hash code of track (it is unique in case of bulk upload)
     * @param trackDate - date of track
     * @return trackId
     */
    @RequestMapping(value = WSConstants.WS_GETTRACK, 
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            headers = "content-type=application/x-www-form-urlencoded")
    public @ResponseBody ResponseEntity<String> getTrack(@RequestParam String car,
                                                       @RequestParam String trackDate ) {
        return new ResponseEntity<String>(gson.toJson(service.getTrackId(car, trackDate)), HttpStatus.OK);
    }
}
