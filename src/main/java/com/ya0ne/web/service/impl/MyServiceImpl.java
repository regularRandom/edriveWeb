package com.ya0ne.web.service.impl;

import static com.ya0ne.core.constants.Constants.DEFAULT_CLOUD_ACCOUNT;
import static com.ya0ne.core.constants.Constants.DEFAULT_CLOUD_ACCOUNT_PASSWORD;
import static com.ya0ne.core.constants.Constants.DEFAULT_CLOUD_WEBDAV_LOCATION;
import static com.ya0ne.core.constants.Constants.DIR_CLOUD_DATA;
import static com.ya0ne.core.constants.Constants.FILE_CURRENT_COUNTRY;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_ERROR;
import static com.ya0ne.core.constants.WebConstants.WEB_UPLOAD_OK;
import static com.ya0ne.core.domain.converters.car.CarConverter.toMyCarDtos;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.google.gson.Gson;
import com.ya0ne.core.domain.Account;
import com.ya0ne.core.domain.Country;
import com.ya0ne.core.domain.car.MyCar;
import com.ya0ne.core.domain.converters.CountryConverter;
import com.ya0ne.core.domain.dao.AccountDAO;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.exceptions.ServiceException;
import com.ya0ne.core.factories.ConfigurationFactory;
import com.ya0ne.core.factories.CountryFactory;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.utilities.CommonUtilities;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.web.service.MyService;

@Service
public class MyServiceImpl implements MyService {
    private static Logger logger = Logger.getLogger(MyServiceImpl.class);
    private Sardine sardineFrom;
    private Sardine sardineTo;
    private String webCloudDataDir;
    private Gson gson = new Gson();
    private static String defaultCar;
    private static String defaultCarPassword;

    // DAOs
    @Autowired private AccountDAO accountDao;

    // Converters
    @Autowired private CountryConverter countryConverter;

    // Factories
    @Autowired private ConfigurationFactory configuration;
    @Autowired private CountryFactory countryFactory;
    @Autowired private LanguageFactory languages;

    @Autowired private DomainUtilities domainUtilities;

    @PostConstruct
    private void init() {
        webCloudDataDir = configuration.getConfigurationValue(DEFAULT_CLOUD_WEBDAV_LOCATION) + DIR_CLOUD_DATA;
        defaultCar = configuration.getConfigurationValue(DEFAULT_CLOUD_ACCOUNT);
        defaultCarPassword = configuration.getConfigurationValue(DEFAULT_CLOUD_ACCOUNT_PASSWORD);
    }
    
    @Override
    public ModelAndView getMyAccount( Locale locale, ModelAndView mv ) {
        mv.addObject("myCars", toMyCarDtos(domainUtilities.getAccount().getCars()));
        mv.addObject("myParameters", domainUtilities.getAccount().getParameters().get(languages.getLanguage(locale.getLanguage())));
        mv.addObject("myAccountData", domainUtilities.getAccount());
        mv.addObject("countries",countryConverter.toCountryDtoList(countryFactory.getCountryList(),locale));
        mv.addObject("countriesWithTimeZones",countryConverter.toCountryDtoListWithTimeZones(countryFactory.getCountryList(), locale));
        //mv.addObject("makes",makeConverter.toMakeDtos(carFactory.getMakesList(),locale));
        return mv;
    }

    @Override
    public boolean changePassword( String newPassword, HttpServletRequest request ) {
        if( CommonUtilities.isValidMD5( newPassword ) ) {
            HttpSession session = request.getSession(false);
            Account account = (Account)session.getAttribute("account");
            account.setPassword(newPassword);
            try {
                accountDao.update(account);
                return true;
            } catch( DAOException e ) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public ModelAndView getEditAccount(Locale locale) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("myAccountData", domainUtilities.getAccount());
        return mv;
    }

    @Override
    public Object[] loadTimeZones( String country ) throws ServiceException {
        List<MyCar> cars = domainUtilities.getAccount().getCars();
        Object[] result = new Object[cars.size()+1];
        String[] countriesList = country.split(",");

        result[0] = countriesList;
        result[1] = cars;

        return result;
    }

    @Override
    public String serializeCurrentCountry( long countryId ) {
        Country country = countryFactory.getCountryValue(countryId);
        List<MyCar> cars = domainUtilities.getAccount().getCars();

        try { 
            for( MyCar car : cars ) {
                sardineTo = SardineFactory.begin(car.getVinCode(), car.getPassword());
                sardineTo.enableCompression();
                CommonUtilities.saveToCloud( sardineTo, webCloudDataDir + File.separator + FILE_CURRENT_COUNTRY, country );
                logger.info("Current country " + country.getIsoCode2() + " has been serialized to cloud: " + webCloudDataDir + File.separator + FILE_CURRENT_COUNTRY);
            }
        }catch( Exception e ) {
            logger.error("Error serializing current country to cloud: " + e.getMessage());
            return WEB_UPLOAD_ERROR;
        }
        return WEB_UPLOAD_OK; 
    }
}
