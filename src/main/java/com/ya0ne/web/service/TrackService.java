package com.ya0ne.web.service;

import java.util.Locale;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.exceptions.CloudException;

public interface TrackService {
    /**
     * Fetches all tracks of customers and shows them in a grid
     * @param locale
     * @return
     */
    ModelAndView getTracks( Locale locale );
    
    /**
     * Another one method to get tracks list
     * @return JSON string
     */
    String getTracksList();
    
    /**
     * Shows single track data
     * @param trackId
     * @param locale
     * @return
     */
    ModelAndView showTrack( long trackId, Locale locale, HttpServletRequest request );

    /**
     * Fetches data from database and returns Google Map with track
     */
    String prepareTrackMap( long trackId, HttpServletRequest request );

    /**
     * Creates view to choose tracks to be merged
     * @return ModelAndView object with list of tracks and controls
     */
    ModelAndView mergeTracksView( Locale locale );
    
    /**
     * Shows view with upload track dialog
     * @return
     */
    ModelAndView uploadTrackView();
    
    /**
     * Uploads new track from the backlog file
     * @param MultipartFile backlogFile (NMEA format or GPX)
     * @return ModelAndView - /my/tracks
     */
    String uploadTrackFile( MultipartFile backlogFile, long carId );

    /**
     * Merges array of tracks into the first one
     * @param tracksIds
     * @return long (trackId)
     */
    Long mergeTracks( String tracksIds );

    /**
     * Deletes tracks with trackId
     * @param trackIds as JSON string
     * @return number of tracks deleted
     */
    int deleteTracks( String tracksIds );

    /**
     * Deletes track points with trackPointIds
     * @param trackPointId[]
     * @return number of track points deleted
     */
    int deleteTrackPoints(String trackPointIds);

    /**
     * Update track's readOnly/publicTrack parameters
     * @param trackId
     * @param field - name of field to be updated
     * @param value - true|false
     * @return number of records updated
     */
    int updateTrack(Long trackId, String field, Boolean value );
    
    /**
     * Update track's description
     * @param trackId
     * @param description
     * @return number of records updated
     */
    int updateTrack(Long trackId, String description );
    
    /**
     * Fetches all public tracks of all customers and shows them in a grid
     * @param locale
     * @return
     */
    ModelAndView getPublicTracks( Locale locale );

    /**
     * Another one method to get all public tracks list
     * @return JSON string
     */
    String getPublicTracksList();

    /**
     * Returns ModelAndView of checkBacklog
     * @param locale
     * @return
     */
    ModelAndView checkBacklog( Locale locale );

    /**
     * Returns JSON representation of backlog list
     * @return JSON string
     */
    String getBacklog();

    /**
     * Returns OK
     * @return
     */
    String loadBacklog( String backlog );

    /**
     * Deletes selected backlog entries. In case of no params supplied, all backlog entries will be deleted
     * @param backlog (JSON string)
     * @return number of deleted tracks
     */
    int clearBacklog( String backlog );
    
    /**
     * Recalculates track
     * @param trackId
     * @return trackId (the same if success, 0 - if error)
     */
    long recalcTrack( long trackId );

    /**
     * Exports track to GPX format
     * @param trackId
     * @return byte array of GPX data 
     */
    byte[] exportGpx( long trackId );

    /**
     * Exports track to NMEA format
     * @param trackId
     * @return byte array of NMEA data 
     */
    byte[] exportNmea( long trackId );

    /**
     * Returns track date in backlogFilenameFormat
     * @return String
     */
    String getTrackDate();

    /**
     * Archives array of tracks into the cloud
     * @param tracksIds
     * @return long (trackId)
     */
    void archiveTracks( String tracksIds ) throws CloudException, JMSException;

    /**
     * Loads tracks from the cloud
     * @param tracksIds
     * @throws CloudException
     * @throws JMSException
     */
    void extractTracks( String tracksIds ) throws CloudException, JMSException;

    /**
     * Creates empty track 
     * @param carId
     * @param date
     * @param loadType
     * @return trackId
     * @throws TrackErrorException
     */
    //long getTrack( long carId, Date date, String loadType ) throws TrackErrorException;
}
