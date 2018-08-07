package com.ya0ne.web.service.impl;

import static com.ya0ne.core.constants.Constants.DEFAULT_CLOUD_WEBDAV_LOCATION;
import static com.ya0ne.core.constants.Constants.DIR_BACKLOG;
import static com.ya0ne.core.constants.Constants.fileGpx;
import static com.ya0ne.core.constants.Constants.fileNmea;
import static com.ya0ne.core.constants.WebConstants.WEB_MY;
import static com.ya0ne.core.constants.WebConstants.WEB_TRACKS;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_ERROR;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_OK;
import static com.ya0ne.core.constants.WebConstants.WEB_WEBDAV_ERROR;
import static com.ya0ne.core.domain.converters.TrackConverter.toTracksListDto;
import static com.ya0ne.core.domain.converters.TrackDataConverter.toTrackPointDtos;
import static com.ya0ne.core.utilities.CommonUtilities.toIds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ya0ne.core.domain.BacklogRecord;
import com.ya0ne.core.domain.Parameter;
import com.ya0ne.core.domain.car.MyCar;
import com.ya0ne.core.domain.dao.TrackDAO;
import com.ya0ne.core.domain.dao.TrackPointDAO;
import com.ya0ne.core.domain.dao.car.CarDAO;
import com.ya0ne.core.domain.enums.TrackType;
import com.ya0ne.core.domain.track.Track;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.factories.ConfigurationFactory;
import com.ya0ne.core.factories.EntityFactory;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.factories.ParameterFactory;
import com.ya0ne.core.generated.DataForDeleteDto;
import com.ya0ne.core.generated.TrackPointDto;
import com.ya0ne.core.generated.TracksListDto;
import com.ya0ne.core.properties.CommonProperties;
import com.ya0ne.core.utilities.CommonUtilities;
import com.ya0ne.core.utilities.DateUtilities;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.core.utilities.HibernateProxyTypeAdapter;
import com.ya0ne.job.handlers.ArchiveTrackHandler;
import com.ya0ne.job.handlers.ExtractTrackHandler;
import com.ya0ne.job.handlers.JobHandler;
import com.ya0ne.job.handlers.SaveBacklogTrackHandler;
import com.ya0ne.job.handlers.SaveGPXTrackHandler;
import com.ya0ne.job.handlers.SaveNMEATrackHandler;
import com.ya0ne.job.utilities.QueueUtilities;
import com.ya0ne.np.misc.GPXProcessor;
import com.ya0ne.web.service.TrackService;

@Service
public class TrackServiceImpl implements TrackService {
    private static Logger logger = Logger.getLogger(TrackServiceImpl.class);
    private Sardine sardine;
    private String trackDate;
    private Gson gson = new Gson();
    private GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
    private String webBacklogDir;

    @PostConstruct
    private void init() {
        webBacklogDir = configuration.getConfigurationValue(DEFAULT_CLOUD_WEBDAV_LOCATION) + DIR_BACKLOG;
    }

    @Autowired private QueueUtilities queueUtilities;
    @Autowired private CarDAO carDao;
    @Autowired private TrackDAO trackDao;
    @Autowired private TrackPointDAO trackPointDao;
    @Autowired private DomainUtilities domainUtilities;
    @Autowired private ApplicationContext context;
    @Autowired private LanguageFactory languages;
    @Autowired private ParameterFactory parameters;
    @Autowired private ConfigurationFactory configuration;
    @Autowired private GPXProcessor trackGpx;
    @Autowired private EntityFactory entityFactory;
    @Autowired private CommonUtilities commonUtilities;
    @Autowired private CommonProperties commonProperties;

    @Override
    public ModelAndView getTracks( Locale locale ) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("parameters",domainUtilities.getAccount().getParameters().get(languages.getLanguage(locale.getLanguage())));
        return mv;
    }

    @Override
    public String getTracksList() {
        List<TracksListDto> list = toTracksListDto(trackDao.getTracksForCustomer(domainUtilities.getCustomerId(),commonProperties.getThresholdToMerge()));
        return gson.toJson(list);
    }

    @Override
    public ModelAndView showTrack( long trackId, Locale locale, HttpServletRequest request ) {
        ModelAndView mv = new ModelAndView();
        Track track = null;
        Map<String, Parameter> parametersMap = null;

        if( request.getServletPath().startsWith(WEB_TRACKS) ) {
            mv.setViewName(WEB_TRACKS + "/singleTrack");
            track = trackDao.getTrack(trackId);
            parametersMap = parameters.getParameters().get(languages.getLanguage(locale.getLanguage()));
        } else {
            mv = new ModelAndView(WEB_MY + "/singleTrack");
            track = trackDao.getTrack(trackId,domainUtilities.getCustomerId());
            parametersMap = domainUtilities.getAccount().getParameters().get(languages.getLanguage(locale.getLanguage()));
        }

        mv.addObject("track",track);
        mv.addObject("parameters", parametersMap);
        return mv;
    }

    /**
     * @see com.ya0ne.web.service.TrackService
     */
    @Override
    public String prepareTrackMap( long trackId, HttpServletRequest request ) {
        List<TrackPointDto> list = new ArrayList<>();
        if( request.getServletPath().startsWith("/tracks") ) {
            list = toTrackPointDtos(trackDao.getTrackData(trackId));
        } else {
            list = toTrackPointDtos(trackDao.getTrackData(trackId,domainUtilities.getCustomerId()));
        }
        return gsonBuilder.setDateFormat(DateUtilities.displayDateTimeFormatStr2).create().toJson(list);
    }

    @Override
    public ModelAndView mergeTracksView( Locale locale ) {
        ModelAndView mv = new ModelAndView("my/mergeTracks");
        mv.addObject("parameters",domainUtilities.getAccount().getParameters().get(languages.getLanguage(locale.getLanguage())));
        return mv;
    }

    @Override
    public ModelAndView uploadTrackView() {
        ModelAndView mv = new ModelAndView("my/uploadTrack");
        return mv;
    }

    @Override
    @Async
    public String uploadTrackFile( MultipartFile backlogFile, long carId ) {
        String ext = FilenameUtils.getExtension(backlogFile.getOriginalFilename()).toLowerCase();
        try {
            File file = commonUtilities.multipartToFile(backlogFile);
            long customerId = carId;
            JobHandler handler = null;
            TrackType trackType = TrackType.trackTypeAuto;
            if( carId <= 0 ) {
                customerId = domainUtilities.getCustomerId();
                switch( (int)carId ) {
                    case 0 : trackType = TrackType.trackTypePedestrian; break;
                    case -1 : trackType = TrackType.trackTypeBicycle; break;
                }
            }
            switch( ext ) {
                case fileNmea : // NMEA (raw NMEA data)
                    handler = (SaveNMEATrackHandler)context.getBean(SaveNMEATrackHandler.class,
                            new Object[]{file,customerId,trackType});
                    queueUtilities.put(handler);
                    break;
                case fileGpx : // GPX
                    handler = (SaveGPXTrackHandler)context.getBean(SaveGPXTrackHandler.class,
                            new Object[]{file,customerId,trackType});
                    queueUtilities.put(handler);
                    break;
                default: 
                    break;
            }
            return WEB_UPLOAD_OK;
        } catch (IllegalStateException | IOException | JMSException e) {
            logger.error(e.getMessage());
            return WEB_UPLOAD_ERROR;
        }
    }

    @Override
    public Long mergeTracks( String tracksIds ) {
        DataForDeleteDto[] dtos = gson.fromJson(tracksIds,DataForDeleteDto[].class);
        if( dtos.length <= 1 ) {
            return null;
        }
        Long trackId = trackDao.mergeTracks(toIds(dtos));
        return trackId;
    }

    @Override
    public int updateTrack(Long trackId, String field, Boolean value) {
        return trackDao.updateTrackProperties(trackId, field, value);
    }

    @Override
    public int updateTrack( Long trackId, String description ) {
        return trackDao.updateTrackDescription(trackId, description);
    }

    @Override
    public ModelAndView getPublicTracks(Locale locale) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("parameters",parameters.getParameters().get(languages.getLanguage(locale.getLanguage())));
        return mv;
    }

    @Override
    public String getPublicTracksList() {
        List<Track> pairs = (List<Track>)trackDao.getPublicTracks();
        return gson.toJson(pairs);
    }

    @Override
    public ModelAndView checkBacklog( Locale locale ) {
        ModelAndView mv = new ModelAndView("/my/backlog");
        return mv;
    }

    @Override
    public String getBacklog() {
        List<MyCar> cars = domainUtilities.getAccount().getCars();
        ArrayList<BacklogRecord> listOfBacklog = new ArrayList<>();
        for( int i = 0; i < cars.size(); i++ ) {
            MyCar car = cars.get(i);
            logger.info("Working with: " + car.getName());
            Sardine sardine = SardineFactory.begin(car.getVinCode(), car.getPassword());
            try {
                List<DavResource> resources;
                resources = sardine.list(webBacklogDir);
                resources.forEach((res)->{
                    if( !res.getName().equals("backlog") ) {
                        listOfBacklog.add(new BacklogRecord(res.getName(),DateUtilities.dateTime2(res.getModified()), res.getContentLength().toString(), car));
                    }
                });
            } catch (IOException e) {
                logger.error(webBacklogDir + " is unavailable, try again later");
                return null;
            }
        }
        return gson.toJson(listOfBacklog);
    }

    @Override
    public String loadBacklog( String backlog ) {
        try {
            JobHandler handler = (SaveBacklogTrackHandler)context.getBean(SaveBacklogTrackHandler.class,
                    new Object[]{domainUtilities.getAccount().getId(),backlog});
            queueUtilities.put(handler);
        } catch (JMSException e) {
            logger.error(e.getMessage());
            return WEB_UPLOAD_ERROR;
        }
        return WEB_UPLOAD_OK;
    }

    @Override
    public int clearBacklog( String deleteId ) {
        List<DavResource> resources = new ArrayList<>();
        List<MyCar> cars = domainUtilities.getAccount().getCars();
        DataForDeleteDto[] dtos = gson.fromJson(deleteId, DataForDeleteDto[].class);

        for( MyCar car : cars ) {
            sardine = SardineFactory.begin(car.getVinCode(), car.getPassword());
            try {
                resources = sardine.list(webBacklogDir);
                for (DavResource res : resources) {
                    if( !res.getName().equals("backlog") ) {
                        if( dtos.length > 0 && !elementIn( dtos, res.getName() ) ) {
                            continue;
                        }
                        deleteBacklogEntry( res );
                    }
                }
            } catch (IOException e) {
                return WEB_WEBDAV_ERROR;
            }
        }
        return resources.size();
    }

    /**
     * Deletes backlog entry
     * @param DavResource resource
     * @throws IOException
     */
    private void deleteBacklogEntry( DavResource resource ) throws IOException {
        sardine.delete(webBacklogDir + "/" + resource.getName());
    }

    /**
     * Returns true if element is in the BacklogRecord[] array
     * @param BacklogRecord[] array
     * @param String match
     * @return true|false
     */
    private boolean elementIn( DataForDeleteDto[] array, String match ) {
        for( int i = 0; i<array.length; i++ ) {
            if( array[i].getId().equals(match) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long recalcTrack(long trackId) {
        return trackDao.recalcTrack( trackId );
    }

    @Override
    public byte[] exportGpx(long trackId) {
        Track track = new Track();
        track.setTrack(trackDao.getTrackData(trackId,domainUtilities.getCustomerId()));
        logger.info("Track ID: " + track.getId() + ", size: " + track.getTrack().size());
        return trackGpx.exportTrack(track);
    }

    @Override
    public int deleteTrackPoints(String trackPoints) {
        DataForDeleteDto[] dtos = gson.fromJson(trackPoints,DataForDeleteDto[].class);
        if( dtos.length == 0 ) {
            return 0;
        }
        return trackPointDao.deleteTrackPoints(toIds(dtos));
    }

    @Override
    public byte[] exportNmea(long trackId) {
        Track track = trackDao.getTrack(trackId,domainUtilities.getCustomerId());
        track.setTrack(trackDao.getTrackData(trackId,domainUtilities.getCustomerId()));
        this.trackDate = DateUtilities.backlogFilename(track.getTrackDate());
        logger.info("Track ID: " + trackId + ", size: " + track.getTrack().size());
        return trackGpx.exportTrackToNmea(track);
    }

    public String getTrackDate() {
        return trackDate;
    }

    @Override
    public void archiveTracks( String tracksIds) throws JMSException, CloudException {
        List<Long> trackIds = toIds(gson.fromJson(tracksIds,DataForDeleteDto[].class));
        trackIds.forEach(trackId->{
            Track track = trackDao.getTrack(trackId,domainUtilities.getCustomerId());
            MyCar myCar = carDao.getCar(track.getCustomerId());
            trackDao.updateTrackStatus(trackId, entityFactory.getEntityId("ENTITYSTATUS", "LOCKED"));
            JobHandler handler = (ArchiveTrackHandler)context.getBean(ArchiveTrackHandler.class,
                    new Object[]{myCar.getVinCode().toUpperCase(),
                                 myCar.getPassword(),
                                 trackId,
                                 domainUtilities.getCustomerId()});
            try {
                queueUtilities.put(handler);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Override
    public void extractTracks(String tracksIds) throws CloudException, JMSException {
        List<Long> trackIds = toIds(gson.fromJson(tracksIds,DataForDeleteDto[].class));
        trackIds.forEach(trackId->{
            try {
                Track track = trackDao.getTrack(trackId,domainUtilities.getCustomerId());
                MyCar myCar = carDao.getCar(track.getCustomerId());
                trackDao.updateTrackStatus(trackId, entityFactory.getEntityId("ENTITYSTATUS", "LOCKED"));
                JobHandler handler = (ExtractTrackHandler)context.getBean(ExtractTrackHandler.class,
                        new Object[]{myCar.getVinCode().toUpperCase(),
                                     myCar.getPassword(),
                                     trackId});
                queueUtilities.put(handler);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Override
    public int deleteTracks(String tracksIds) {
        DataForDeleteDto[] dtos = gson.fromJson(tracksIds,DataForDeleteDto[].class);
        if( dtos.length == 0 ) {
            return 0;
        }
        return trackDao.deleteTracks(toIds(dtos));
    }
}
