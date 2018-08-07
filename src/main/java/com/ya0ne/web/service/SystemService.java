package com.ya0ne.web.service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.domain.Country;
import com.ya0ne.core.exceptions.CSVUploadException;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.exceptions.ConfigurationException;
import com.ya0ne.core.exceptions.TranslationException;

public interface SystemService {

    /**
     * Shows main service page
     * @param locale
     * @return ModelAndView
     */
    ModelAndView getService( Locale locale );

    /**
     * performs upload of CSV file from GeoNames.org
     * @param MultipartFile
     * @return Country - pointer to the country uploaded
     */
    Country uploadGeoTZCSV( MultipartFile geoTZFile ) throws CSVUploadException;

    /**
     * Returns list of all configuration entities
     * @return JSON string
     */
    String getConfigurations();

    /**
     * Returns configuration entity
     * @return JSON string
     */
    String getConfiguration( String key );

    /**
     * Saves configuration entity
     * @return JSON string (OK or FAIL)
     */
    void saveConfiguration( String configuration ) throws ConfigurationException;

    /**
     * Updates configuration
     * @return JSON string (OK or FAIL)
     */
    void updateConfiguration( String configuration, String oldKey ) throws ConfigurationException;

    /**
     * Reloads ConfigurationFactory
     */
    void reloadConfigurations();

    /**
     * Deletes Configuration entity
     * @throws ConfigurationException
     */
    void deleteConfiguration( String key ) throws ConfigurationException;


    /**
     * Returns list of all text entities and translations
     * @return JSON string
     */
    String getTranslations();

    /**
     * Returns text entity and its translation
     * @return JSON string
     */
    String getTranslation( String dataIds );

    /**
     * Saves translation
     * @return JSON string (OK or FAIL)
     */
    void saveTranslation( String translation ) throws TranslationException;

    /**
     * Updates translation
     * @return JSON string (OK or FAIL)
     */
    void updateTranslation( String translation, String dataIds ) throws TranslationException;

    /**
     * Reloads DatabaseMessageSource
     */
    void reloadTranslations();

    /**
     * Deletes TextEntity & TranslationEntity
     * @throws TranslationException
     */
    void deleteTranslation( String dataIds ) throws TranslationException;

    /**
     * performs upload of CSV file with admin codes from GeoNames.org
     * @param MultipartFile
     * @throws CSVUploadException
     */
    void uploadDistrictsCSV( MultipartFile districtFile ) throws CSVUploadException;

    /**
     * performs upload of CSV file with admin codes (2) from GeoNames.org
     * @param MultipartFile
     * @throws CSVUploadException
     */
    void uploadDistrictsCSV2( MultipartFile districtFile ) throws CSVUploadException;

    /**
     * loads CSV-file and creates map with countries' boundaries
     * @param countriesBoundariesFile
     * @return String - status
     */
    Map<Country,Set<Country>> loadCountriesBoundaries( MultipartFile countriesBoundariesFile ) throws CSVUploadException;

    /**
     * Uploads list of countries to cloud as countries.lst
     * @throws CloudException
     */
    void refreshCountries() throws CloudException;

    /**
     * This method creates a map of time zones and serializes it to the cloud
     * @param locale
     * @throws CloudException
     */
    void loadTimeZones() throws CloudException;
}
