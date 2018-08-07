package com.ya0ne.web.service.ws.impl;

import java.util.Arrays;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ya0ne.core.constants.WSConstants;
import com.ya0ne.core.domain.converters.GPXConverter;
import com.ya0ne.core.domain.converters.XMLGregorianCalendarConverter;
import com.ya0ne.core.domain.dao.AccountDAO;
import com.ya0ne.core.domain.dao.TrackDAO;
import com.ya0ne.core.domain.enums.TrackType;
import com.ya0ne.core.domain.track.Track;
import com.ya0ne.core.domain.track.TrackPoint;
import com.ya0ne.core.factories.EntityFactory;
import com.ya0ne.core.generated.NmeaSentenceDto;
import com.ya0ne.core.utilities.DateUtilities;
import com.ya0ne.core.utilities.HibernateProxyTypeAdapter;
import com.ya0ne.core.utilities.TrackUtilities;
import com.ya0ne.web.service.ws.NMEAService;

import static com.ya0ne.core.constants.Constants.trackLoadTypeOnline;
import static com.ya0ne.core.constants.Constants.trackStatusActive;
import static com.ya0ne.core.constants.WSConstants.WS_NOCARFOUND;

@Service
public class NMEAServiceImpl implements NMEAService {
    private static Logger logger = Logger.getLogger(NMEAServiceImpl.class);

    @Autowired private AccountDAO accountDao;
    @Autowired private TrackDAO trackDao;
    @Autowired private ApplicationContext context;
    @Autowired private GPXConverter gpxConverter;
    @Autowired private TrackUtilities trackUtilities;
    @Autowired private EntityFactory entityFactory;

    private Object[] nmeaSentenceDtos = new NmeaSentenceDto[7];

    private Gson gson = new GsonBuilder().
            registerTypeAdapter(XMLGregorianCalendar.class,new XMLGregorianCalendarConverter.Serializer()).
            registerTypeAdapter(XMLGregorianCalendar.class,new XMLGregorianCalendarConverter.Deserializer()).
            registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).
            setDateFormat(WSConstants.NMEA_DATE_FORMAT).
            create();

    /**
     * Method parses of input NMEA string and saves data into the database
     * @param nmeaSentence
     * @return
     */
    public int processNmeaSentence( String nmeaStr ) {
        NmeaSentenceDto[] nmeaSentenceDto = gson.fromJson(nmeaStr, NmeaSentenceDto[].class);
        for( int i = 0; i<nmeaSentenceDto.length; i++ ) {
            if( !Double.isInfinite(nmeaSentenceDto[i].getLat()) && !Double.isInfinite(nmeaSentenceDto[i].getLon()) ) {
                nmeaSentenceDtos = gpxConverter.toTrackPointData( nmeaSentenceDto[i] );
            	logger.info("NMEA sentence: " + Arrays.toString(nmeaSentenceDtos));
            	TrackPoint trkpt = (TrackPoint)context.getBean("trackPoint",nmeaSentenceDtos);
            	trackDao.saveTrackPoint(trkpt);
            } else {
            	logger.error(WSConstants.NMEA_NO_GPS_SIGNAL + ", latitude: " + nmeaSentenceDto[i].getLat() + ", longitude: " + nmeaSentenceDto[i].getLon());
            }
        }
        nmeaSentenceDto = null;
        return WSConstants.WS_OK;
    }

    /**
     * Creates new track and returns track ID from the database
     * @return String trackId
     */
    public Long getTrackId( String producer, String trackDate ) {
        long producerId = accountDao.getAccountIdByCar(producer);
        if( producerId != WS_NOCARFOUND ) {
            Track track = trackDao.createTrack(trackUtilities.getTrack(producerId, DateUtilities.trackDateTime(trackDate), trackLoadTypeOnline, TrackType.trackTypeAuto));
            trackDao.updateTrackStatus(track.getId(), entityFactory.getEntityId("ENTITYSTATUS", trackStatusActive));
            logger.info("Track has been created with ID = " + track.getId());
            return track.getId();
        }
        return null;
    }
}
