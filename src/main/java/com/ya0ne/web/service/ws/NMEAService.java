package com.ya0ne.web.service.ws;

public interface NMEAService {
    /**
     * Saves NMEA sentence into the database
     * @param trackId
     * @param nmeaStr
     * @return
     */
    public int processNmeaSentence( String nmeaStr );
    
    /**
     * Creates new track and returns its ID (AES encrypted)
     * @param car
     * @param hashCode
     * @param trackDate
     * @return
     */
    public Long getTrackId( String car, String trackDate );
}
