package com.ya0ne.web.aop;

import static com.ya0ne.core.constants.Constants.DIR_TZDATA;
import static com.ya0ne.core.constants.Constants.FILE_BOUNDARIES_MAP;
import static com.ya0ne.core.constants.Constants.FILE_COUNTRIES_LIST;
import static com.ya0ne.core.constants.Constants.FILE_TIMEZONES;

import java.io.File;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import com.ya0ne.core.domain.Country;
import com.ya0ne.core.domain.cloud.CloudBean;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.factories.CountryFactory;
import com.ya0ne.core.factories.TimeZoneFactory;
import com.ya0ne.core.geo.dao.GeoProcessorDAO;
import com.ya0ne.core.utilities.CommonUtilities;

@Aspect
public class UploadToDefaultAspect extends CloudBean {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(UploadToDefaultAspect.class);

    @Autowired private GeoProcessorDAO geoService;
    @Autowired private CountryFactory countryFactory;
    @Autowired private TimeZoneFactory timeZoneFactory;

    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.SystemService.uploadGeoTZCSV(..)) || execution(* com.ya0ne.misc.*CloudProcessor.uploadGeoTZCSV(..))",
                    returning = "country")
    public void uploadGeoTZCSVAfterReturning( JoinPoint joinPoint, Object country ) throws CloudException {
        if( country != null  ) {
            geoService.createTZ(String.valueOf(((Country)country).getId()));
            String cntry = ((Country)country).getIsoCode2();
            CommonUtilities.saveToCloud(defaultSardine, webCloudDataDirDefault + File.separator + DIR_TZDATA + File.separator + cntry + ".tz", geoService.prepareTree());
        } else {
            throw new CloudException();
        }
    }

    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.SystemService.loadCountriesBoundaries(..))",
                    returning = "countries")
    public void loadCountriesBoundariesAfterReturning( JoinPoint joinPoint, Object countries ) throws CloudException {
        if( countries != null  ) {
            CommonUtilities.saveToCloud( defaultSardine, webCloudDataDirDefault + File.separator + FILE_BOUNDARIES_MAP, countries );
        } else {
            throw new CloudException();
        }
    }

    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.SystemService.refreshCountries(..))")
    public void refreshCountriesAfterReturning( JoinPoint joinPoint ) throws CloudException {
        CommonUtilities.saveToCloud( defaultSardine, webCloudDataDirDefault + File.separator + FILE_COUNTRIES_LIST, countryFactory.getCountryMap() );
    }

    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.SystemService.loadTimeZones(..))")
    public void refreshTimeZonesAfterReturning( JoinPoint joinPoint ) throws CloudException {
        CommonUtilities.saveToCloud( defaultSardine, webCloudDataDirDefault + File.separator + FILE_TIMEZONES, timeZoneFactory.getTimeZoneMap() );
    }
}
