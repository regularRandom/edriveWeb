package com.ya0ne.web.controller;

import static com.ya0ne.core.constants.Constants.OK;
import static com.ya0ne.core.constants.Constants.fileNmea;
import static com.ya0ne.core.constants.WebConstants.WEB_EMPTY_TRACK_LIST;
import static com.ya0ne.core.constants.WebConstants.WEB_MYTRACKS;
import static com.ya0ne.core.constants.WebConstants.WEB_STATUS_OK;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_OK;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.i18n.DatabaseMessageSource;
import com.ya0ne.core.utilities.http.HttpUtilities;
import com.ya0ne.web.service.TrackService;

@Controller
@Secured({"ROLE_USER","ROLE_ADMIN"})
@RequestMapping(WEB_MYTRACKS)
public class TracksController {
	private static Logger logger = Logger.getLogger(TracksController.class);
	private Gson gson = new Gson();

	@Autowired private DatabaseMessageSource messageSource;
    @Autowired private TrackService trackService;

    /**
     * Shows list of tracks
     * @param locale
     * @return ModelAndView
     */
    @GetMapping
    public ModelAndView showTracks( Locale locale ) {
        return trackService.getTracks( locale, WEB_MYTRACKS );
    }

    /**
     * returns JSON representation of list of tracks
     * @return JSON string
     */
    @PostMapping(value="/getTracksList", 
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String getTracksList() {
        return trackService.getTracksList();
    }
    
    /**
     * Returns ModelAndView object with track data
     * @param id
     * @param locale
     * @param request
     * @return ModelAndView
     */
    @GetMapping(value="/show/{id}")
    public ModelAndView showTrack( @PathVariable("id") Long id, Locale locale, HttpServletRequest request ) {
        return trackService.showTrack( id, locale, request );
    }

    /**
     * Method returns JSON representation of track
     * @param id
     * @return JSON string
     */
    @PostMapping(value="/populateTrack/{id}", 
  				 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String populateTrack( @PathVariable("id") Long id, HttpServletRequest request ) {
        return trackService.prepareTrackMap( id, request );
    }
    
    /**
     * Method returns a view with tracks ready for merge
     * @param locale
     * @return ModelAndView
     */
    @GetMapping(value="/mergeTracks")
    public ModelAndView mergeTracks( Locale locale ) {
        return trackService.mergeTracksView( locale );
    }

    /**
     * Calls SPMergeTracks and returns ID of main track.
     * @param trackIds
     * @return
     */
    @PostMapping(value="/merge",
    			 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> merge( @RequestParam(value = "dataIds", required = true ) String dataIds ) {
    	logger.info("TracksController: merge, " + dataIds);
    	Long trackId = trackService.mergeTracks( dataIds );
    	if( trackId == null ) {
    		return new ResponseEntity<String>(gson.toJson(WEB_EMPTY_TRACK_LIST),HttpStatus.NO_CONTENT);
    	} else {
    		return new ResponseEntity<String>(gson.toJson(trackId),HttpStatus.OK);
    	}
    }
    
    /**
     * Method shows view with upload track dialog
     * @return ModelAndView
     */
    // TODO: several types of errors
    @GetMapping(value="/uploadTrack")
    public ModelAndView uploadTrackView(@RequestParam(value = "uploadError", required = false) String uploadError,
    									Locale locale) {
    	ModelAndView mv = new ModelAndView();
    	mv = trackService.uploadTrackView();
    	if( uploadError != null ) {
            mv.addObject("uploadError", uploadError);
        }
        return mv;
    }
    
    /**
     * Uploads new track from backlog- or GPX-file
     * @param backlogFile - backlog from NMEAProcessor or GPX
     * @param locale
     * @return
     */
    @PostMapping(value="/uploadTrackFile")
    @ResponseBody
    public ModelAndView uploadTrackFile( @RequestParam("backlogFile") MultipartFile backlogFile,
    									                 @RequestParam("carId") long carId,
    									                 Locale locale ) {
        ModelAndView mv = new ModelAndView("redirect:" + WEB_MYTRACKS + "/uploadTrack");
        if( backlogFile.isEmpty() ) {
            logger.info("Backlog file is empty or car not selected");
            mv.addObject("uploadError", messageSource.getMessage("TEXT_UPLOAD_BACKLOG_EMPTY_OR_CAR_NOT_SELECTED",null,null,locale));
        } else {
            if( trackService.uploadTrackFile(backlogFile,carId).equals(WEB_UPLOAD_OK) ) {
                mv.setViewName("redirect:" + WEB_MYTRACKS);
            } else {
                mv.addObject("uploadError", messageSource.getMessage("TEXT_UPLOAD_WRONG_GPX_VERSION",null,null,locale));
            }
        }
        return mv;
    }

    /**
     * Updates particular track, readOnly or publicTrack attribute
     * @param trackId
     * @param field - field name: readOnly or publicTrack
     * @param value true/false
     * @return OK or null
     */
    @PostMapping(value="/update",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> update( @RequestParam(value = "trackId", required = true ) long trackId,
		   											    @RequestParam(value = "field", required = true ) String field,
		   											    @RequestParam(value = "value", required = true ) boolean value ) {
        logger.info("TracksController: update, " + trackId + ", field = " + field + ", value = " + value);
        if( trackService.updateTrack( trackId, field, value ) > 0 ) {
            return new ResponseEntity<String>(gson.toJson(OK),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Updates particular track, description
     * @param trackId
     * @param description
     * @return OK or null
     */
    @PostMapping(value="/updateDescription",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> update( @RequestParam(value = "trackId", required = true ) long trackId,
		   											    @RequestParam(value = "description", required = true ) String description ) {
        logger.info("TracksController: update, " + trackId + ", description = " + description );
        if( trackService.updateTrack( trackId, description ) > 0 ) {
            return new ResponseEntity<String>(gson.toJson(OK),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Shows list of tracks
     * @param locale
     * @return ModelAndView
     */
    @GetMapping(value="/checkBacklog")
    public ModelAndView checkBacklog( Locale locale ) {
        return trackService.checkBacklog( locale );
    }

    /**
     * returns JSON representation of list of backlog files
     * @return JSON string
     */
    @PostMapping(value="/getBacklog",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String getBacklog() {
        return trackService.getBacklog();
    }

    /**
     * returns JSON representation of list of backlog files
     * @return JSON string
     */
    @PostMapping(value="/loadBacklog",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> loadBacklog( @RequestParam(value = "backlog", required = true ) String backlog ) {
	    if( trackService.loadBacklog(backlog).equals(WEB_UPLOAD_OK) ) {
		    return new ResponseEntity<String>(gson.toJson(OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""), HttpStatus.NO_CONTENT);
        }
    }

    /**
     * deletes all backlog tracks
     */
    @PostMapping(value="/clearBacklog",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> clearBacklog( @RequestParam( value = "deleteId", required = false ) String deleteId ) {
        if( trackService.clearBacklog( deleteId ) > 0 ) {
            return new ResponseEntity<String>(gson.toJson(OK),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * recalculates track
     */
    @PostMapping(value="/recalcTrack",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> recalcTrack( @RequestParam( value = "trackId", required = true ) long trackId ) {
        if( trackService.recalcTrack( trackId ) != 0 ) {
            return new ResponseEntity<String>(gson.toJson(OK),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""),HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(value = "/exportGpx/{trackId}")
    public void exportGpx( @PathVariable("trackId") long trackId,
                           HttpServletResponse response ) throws IOException {
        response.setContentType("application/gpx");
        response.setHeader( "Content-Disposition", "attachment; filename=" + trackId + ".gpx" );
        InputStream is = new ByteArrayInputStream(trackService.exportGpx(trackId));
        if( is.available() > 0 ) {
            HttpUtilities.copyToFile(is, response);
        }
        response.flushBuffer();
        is.close();
    }

    @GetMapping(value = "/exportNmea/{trackId}")
    public void exportNmea( @PathVariable("trackId") long trackId,
                            HttpServletResponse response ) throws IOException {
        response.setContentType("application/nmea");
        InputStream is = new ByteArrayInputStream(trackService.exportNmea(trackId));
        response.setHeader( "Content-Disposition", "attachment; filename=" + trackService.getTrackDate() + "." + fileNmea );
        if( is.available() > 0 ) {
            HttpUtilities.copyToFile(is, response);
        }
        response.flushBuffer();
        is.close();
    }

    /**
     * Deletes track point and returns OK in case of success, else - null
     * @param trackPointId
     * @return OK
     */
    @PostMapping(value="/deleteTrackPoints",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> deleteTrackPoints( @RequestParam(value = "dataIds", required = true ) String dataIds ) {
        logger.info("TracksController: deleteTrackPoints, " + StringUtils.split(dataIds, ','));
        if( trackService.deleteTrackPoints( dataIds ) > 0 ) {
            return new ResponseEntity<String>(gson.toJson(dataIds),HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(gson.toJson(""),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Deletes track point and returns OK in case of success, else - null
     * @param trackPointId
     * @return OK
     */
    @PostMapping(value="/archive",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> archive( @RequestParam(value = "dataIds", required = true) String dataIds ) {
        logger.info("TracksController: archive, " + dataIds);
        try {
            trackService.archiveTracks( dataIds );
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK);
        } catch( CloudException | JMSException e ) {
            return new ResponseEntity<String>(gson.toJson(WEB_EMPTY_TRACK_LIST),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Deletes track point and returns OK in case of success, else - null
     * @param trackPointId
     * @return OK
     */
    @PostMapping(value="/extract",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> extract( @RequestParam(value = "dataIds", required = true) String dataIds ) {
        logger.info("TracksController: extract, " + dataIds);
        try {
            trackService.extractTracks( dataIds );
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK);
        } catch( CloudException | JMSException e ) {
            return new ResponseEntity<String>(gson.toJson(WEB_EMPTY_TRACK_LIST),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Deletes track point and returns OK in case of success, else - null
     * @param trackPointId
     * @return OK
     */
    @PostMapping(value="/deleteTracks",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> delete( @RequestParam(value = "dataIds", required = true) String dataIds ) {
        logger.info("TracksController: deleteTracks, " + dataIds);
        try {
            trackService.deleteTracks( dataIds );
            return new ResponseEntity<String>(gson.toJson(WEB_STATUS_OK),HttpStatus.OK);
        } catch( Exception e ) {
            return new ResponseEntity<String>(gson.toJson(WEB_EMPTY_TRACK_LIST),HttpStatus.NO_CONTENT);
        }
    }

    /**
     * returns JSON representation of list of tracks
     * @return JSON string
     */
    @PostMapping(value="/archived/list",
                 produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String getArchivedTracksList() {
        return trackService.getArchivedTracksList();
    }

    /**
     * Method returns a view with tracks ready for merge
     * @param locale
     * @return ModelAndView
     */
    @GetMapping(value="/archived")
    public ModelAndView getArchivedTracks( Locale locale ) {
        return trackService.getArchivedTracks( locale );
    }
}
