package com.ya0ne.web.service.impl;

import static com.ya0ne.core.domain.converters.car.CarConverter.toMakeDtos;
import static com.ya0ne.core.domain.converters.car.CarConverter.toMyCarDtos;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.GsonBuilder;
import com.ya0ne.core.domain.Account;
import com.ya0ne.core.domain.car.MyCar;
import com.ya0ne.core.domain.dao.AccountDAO;
import com.ya0ne.core.domain.dao.TrackPointDAO;
import com.ya0ne.core.domain.dao.car.CarDAO;
import com.ya0ne.core.exceptions.CarException;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.factories.CarFactory;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.generated.MyCarForUpdateDto;
import com.ya0ne.core.generated.MyNewCarDto;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.core.utilities.HibernateProxyTypeAdapter;
import com.ya0ne.web.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    @Autowired private CarDAO carDao;
    @Autowired private DomainUtilities domainUtilities;
    @Autowired private TrackPointDAO trackPointDao;
    @Autowired private AccountDAO accountDao;
    @Autowired private CarFactory carFactory;
    @Autowired private LanguageFactory languages;
    private GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

    @Override
    public ModelAndView showModel(Locale locale) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("carsMakes",carDao);
        return mv;
    }

    @Override
    public String getMyCarsList() {
        List<MyCar> cars = domainUtilities.getAccountCars();
        return gsonBuilder.create().toJson(toMyCarDtos(cars));
    }

    @Override
    public MyNewCarDto addCar( String myNewCar, Locale locale ) throws CarException {
        MyNewCarDto myCarDto = gsonBuilder.create().fromJson(myNewCar,MyNewCarDto.class );
        try {
            Long carAccountId = carDao.saveMyCar(myCarDto, domainUtilities.getAccount(), locale);
            Account carAccount = accountDao.getAccount(carAccountId);
            myCarDto.setPassword(carAccount.getPassword());
            domainUtilities.refreshCars();
            return myCarDto;
        } catch (DAOException e) {
            throw new CarException(e.getMessage());
        }
    }

    @Override
    public String getCurrentPosition( long accountId, Locale locale ) {
        return gsonBuilder.create().toJson(trackPointDao.findCurrentTrackPoint(accountId,locale));
    }

    @Override
    public String getCarsList( Locale locale ) {
        return gsonBuilder.create().toJson(toMakeDtos(carFactory.getMakesList(),languages.getLanguage(locale.getLanguage())));
    }

    @Override
    public Long editCar( String myCar ) throws CarException {
        try {
            Long carId = carDao.updateMyCar(gsonBuilder.create().fromJson(myCar, MyCarForUpdateDto.class));
            if( carId.longValue() > 0 ) {
                domainUtilities.refreshCars();
            }
            return carId;
        } catch( DAOException e ) {
            throw new CarException(e.getMessage());
        }
    }
}
