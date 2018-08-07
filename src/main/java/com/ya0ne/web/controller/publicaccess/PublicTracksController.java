package com.ya0ne.web.controller.publicaccess;

import static com.ya0ne.core.constants.WebConstants.WEB_TRACKS;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.web.service.TrackService;

@Controller
@RequestMapping(value=WEB_TRACKS)
public class PublicTracksController {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(PublicTracksController.class);

    @Autowired
    private TrackService trackService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showTracks( Locale locale ) {
        return trackService.getPublicTracks( locale );
    }

    @RequestMapping(value="/getPublicTracksList", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String getPublicTracksList() {
        return trackService.getPublicTracksList();
    }

    @RequestMapping(value="/show/{id}", method = RequestMethod.GET)
    public ModelAndView showTrack( @PathVariable("id") Long id, Locale locale, HttpServletRequest request ) {
        return trackService.showTrack( id, locale, request );
    }

    /**
     * Method returns JSON representation of track
     * @param id
     * @return JSON string
     */
    @RequestMapping(value="/populateTrack/{id}", 
                    method = RequestMethod.POST, 
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String populateTrackMap( @PathVariable("id") Long id, HttpServletRequest request ) {
        return trackService.prepareTrackMap( id, request );
    }
}
