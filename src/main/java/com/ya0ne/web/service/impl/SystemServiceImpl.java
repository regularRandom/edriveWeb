package com.ya0ne.web.service.impl;

import static com.ya0ne.core.constants.Constants.CSV_DELIMITER;
import static com.ya0ne.core.constants.Constants.CSV_DELIMITER2;
import static com.ya0ne.core.domain.converters.ConfigurationConverter.toConfigurationEntity;
import static com.ya0ne.core.domain.converters.ConfigurationConverter.toConfigurationEntityDto;
import static com.ya0ne.core.domain.converters.TextConverter.toTextEntityDto;
import static com.ya0ne.core.domain.converters.TimeZoneDAOConverter.toGeoTimeZone;
import static com.ya0ne.core.domain.converters.TimeZoneDAOConverter.toTimeZones;
import static com.ya0ne.core.utilities.CommonUtilities.toIds;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.ya0ne.core.domain.Configuration;
import com.ya0ne.core.domain.Country;
import com.ya0ne.core.domain.GeoTimeZone;
import com.ya0ne.core.domain.LocalTimeZone;
import com.ya0ne.core.domain.TextEntity;
import com.ya0ne.core.domain.converters.ConfigurationConverter;
import com.ya0ne.core.domain.converters.CountryConverter;
import com.ya0ne.core.domain.converters.DistrictConverter;
import com.ya0ne.core.domain.converters.TextConverter;
import com.ya0ne.core.domain.dao.ConfigurationDAO;
import com.ya0ne.core.domain.dao.CountryDAO;
import com.ya0ne.core.domain.dao.DistrictDAO;
import com.ya0ne.core.domain.dao.TextDAO;
import com.ya0ne.core.domain.dao.TimeZoneDAO;
import com.ya0ne.core.exceptions.CSVUploadException;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.exceptions.ConfigurationException;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.exceptions.TranslationException;
import com.ya0ne.core.factories.ConfigurationFactory;
import com.ya0ne.core.factories.CountryFactory;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.factories.TimeZoneFactory;
import com.ya0ne.core.generated.DataForDeleteDto;
import com.ya0ne.core.i18n.DatabaseMessageSource;
import com.ya0ne.core.utilities.CommonUtilities;
import com.ya0ne.web.service.SystemService;

@Service
@EnableAspectJAutoProxy
public class SystemServiceImpl implements SystemService {
    private static Logger logger = Logger.getLogger(SystemServiceImpl.class);
    private Gson gson = new Gson();

    @Autowired private CountryDAO countryDao;
    @Autowired private CountryConverter countryConverter;
    @Autowired private CountryFactory countryFactory;
    @Autowired private TimeZoneFactory timeZoneFactory;
    @Autowired private TextConverter textConverter;
    @Autowired private DistrictConverter districtConverter;
    @Autowired private TimeZoneDAO timeZoneDao;
    @Autowired private DatabaseMessageSource messageSource;
    @Autowired private LanguageFactory languages;
    @Autowired private TextDAO textDao;
    @Autowired private ConfigurationConverter configurationConverter;
    @Autowired private ConfigurationDAO configurationDao;
    @Autowired private ConfigurationFactory configurationFactory;
    @Autowired private DistrictDAO districtDao;
    @Autowired private CommonUtilities commonUtilities;

    @Override
    public ModelAndView getService( Locale locale ) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("countriesWithTimeZones",countryConverter.toCountryDtoListWithTimeZones(countryFactory.getCountryList(), locale));
        mv.addObject("languages",languages);
        mv.addObject("countries",countryConverter.toCountryDtoList(countryFactory.getCountryList(),locale));
//        mv.addObject("districts",districtConverter.toDistrictDto(districtDao.get(locale), locale));
        return mv;
    }

    @Override
    public Country uploadGeoTZCSV(MultipartFile geoTZFile) throws CSVUploadException {
        File csvData = null;
        try {
            csvData = commonUtilities.multipartToFile(geoTZFile);
            Country country = countryDao.getByIsoCode2(FilenameUtils.getBaseName(geoTZFile.getOriginalFilename()));
            CSVParser parser = CSVParser.parse(csvData, StandardCharsets.UTF_8, CSVFormat.newFormat(CSV_DELIMITER2)); // files from the download.geonames.org/export/dump/ are delimited with \t
            timeZoneDao.deleteAllTZDataForCountry(country); // cleanup before updated list
            Map<TimeZone, LocalTimeZone> ltzMap = toTimeZones(timeZoneDao.loadAll());
            final List<GeoTimeZone> gtzList = new ArrayList<>();

            parser.forEach(csvRecord -> {
                gtzList.add(toGeoTimeZone(csvRecord,country,ltzMap));
            });

            timeZoneDao.insertNewTZList(gtzList); // save to db
            timeZoneDao.updateNullGeoTZLocation(); // updates geotimezones.location with a new data
            timeZoneDao.updateFts(country.getId()); // updates Full Text Search for the country's data
            country.setTimezone(true);
            countryDao.updateCountry(country); // mark country that it has geo-data
            parser.close();
            csvData.delete();
            return country;
        } catch( Exception e ) {
            logger.error(e.getMessage());
            throw new CSVUploadException(e.getMessage());
        }
    }

    @Override
    public String getTranslations() {
        return gson.toJson(toTextEntityDto(messageSource.getTextEntities()));
    }

    @Override
    public void saveTranslation( String translation ) throws TranslationException {
        TextEntity textEntity = textConverter.toTextEntity( translation );
        try {
            textDao.saveEntityTranslation(textEntity);
        } catch( DAOException e ) {
            throw new TranslationException();
        }
    }

    @Override
    public void updateTranslation( String translation, String dataIds ) throws TranslationException {
        TextEntity oldTextEntity = textDao.findById(Arrays.asList(Long.valueOf(dataIds)));
        TextEntity newTextEntity = textConverter.toTextEntity( translation );
        try {
            textDao.deleteTranslation(oldTextEntity);
            textDao.saveEntityTranslation(newTextEntity);
        } catch( DAOException e ) {
            throw new TranslationException();
        }
    }

    @Override
    public void reloadTranslations() {
        messageSource.reload();
    }

    @Override
    public void deleteTranslation( String dataIds ) throws TranslationException {
        DataForDeleteDto[] dtos = gson.fromJson(dataIds,DataForDeleteDto[].class);
        TextEntity textEntity = textDao.findById(toIds(dtos));
        try {
            textDao.deleteTranslation(textEntity);
        } catch( DAOException e ) {
            throw new TranslationException();
        }
    }

    @Override
    public String getTranslation( String dataIds ) {
        TextEntity textEntity = textDao.findById(Arrays.asList(Long.valueOf(dataIds)));
        List<TextEntity> list = new ArrayList<>();
        list.add(textEntity);
        return gson.toJson(toTextEntityDto(list));
    }

    @Override
    public String getConfigurations() {
        return gson.toJson(configurationConverter.toConfigurationDto());
    }

    @Override
    public String getConfiguration(String key) {
        Configuration configurationEntity  = configurationDao.findByKey(key);
        return gson.toJson(toConfigurationEntityDto(configurationEntity));
    }

    @Override
    public void saveConfiguration(String configuration)
            throws ConfigurationException {
        Configuration configurationEntity = toConfigurationEntity( configuration );
        try {
            configurationDao.saveConfiguration(configurationEntity);
        } catch( DAOException e ) {
            throw new ConfigurationException(e.getMessage());
        }

    }

    @Override
    public void updateConfiguration(String configuration, String key)
            throws ConfigurationException {
        Configuration entity = toConfigurationEntity( configuration );
        try {
            configurationDao.updateConfiguration(entity);
        } catch( DAOException e ) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    @Override
    public void reloadConfigurations() {
        configurationFactory.init();
    }

    @Override
    public void deleteConfiguration(String key) throws ConfigurationException {
        Configuration configurationEntity = configurationDao.findByKey(key);
        try {
            configurationDao.deleteConfiguration(configurationEntity);
            configurationFactory.deleteConfigurationEntity(key);
        } catch( DAOException e ) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    @Override
    public void uploadDistrictsCSV(MultipartFile districtFile) throws CSVUploadException {
        try {
            districtDao.clearDistricts();
            uploadDistricts( districtFile );
        } catch(Exception e) {
            logger.error(e.getMessage());
            throw new CSVUploadException( e.getMessage() );
        }
    }

    @Override
    public void uploadDistrictsCSV2(MultipartFile districtFile) throws CSVUploadException {
        try {
            districtDao.clearDistricts2();
            uploadDistricts( districtFile );
        } catch(Exception e) {
            logger.error(e.getMessage());
            throw new CSVUploadException( e.getMessage() );
        }
    }

    private void uploadDistricts( MultipartFile districtFile ) throws CSVUploadException {
        File csvData = null;
        try {
            csvData = commonUtilities.multipartToFile(districtFile);
            CSVParser parser = CSVParser.parse(csvData, StandardCharsets.UTF_8, CSVFormat.newFormat(CSV_DELIMITER2));
            List<CSVRecord> records = parser.getRecords();
            districtDao.saveDistricts(districtConverter.toDistricts(records));
            parser.close();
            csvData.delete();
        } catch( Exception e ) {
            logger.error(e.getMessage());
            throw new CSVUploadException( e.getMessage() );
        }
    }
    
    @Override
    public Map<Country,Set<Country>> loadCountriesBoundaries( MultipartFile countriesBoundariesFile ) throws CSVUploadException {
        File csvData;
        Map<Country,Set<Country>> countries = new HashMap<>();

        try {
            csvData = commonUtilities.multipartToFile(countriesBoundariesFile);
            CSVParser parser = CSVParser.parse(csvData, StandardCharsets.UTF_8, CSVFormat.newFormat(CSV_DELIMITER));
            List<List<String>> countryNames = new ArrayList<>();

            parser.forEach(csvRecord -> { // get list of countries (not boundaries!)
                List<String> list = new ArrayList<>();
                IntStream.range(0, csvRecord.size()).forEach(idx -> {
                    if( !csvRecord.get(idx).equals("") ) {
                        list.add(csvRecord.get(idx));   
                    }
                });
                countryNames.add(list);
            });
            countries = countryConverter.toBoundariesMap(countryNames);
            parser.close();
        } catch( Exception e ) {
            logger.error("Error during parsing input CSV " + countriesBoundariesFile.getOriginalFilename() + ": " + e.getMessage());
            throw new CSVUploadException();
        }
        return countries;
    }

    @Override
    public void refreshCountries() throws CloudException {
        countryFactory.init();
    }

    @Override
    public void loadTimeZones() throws CloudException {
        timeZoneFactory.init();
    }
}
