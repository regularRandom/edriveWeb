package com.ya0ne.web.aop;

import static com.ya0ne.core.constants.Constants.DEFAULT_CLOUD_GROUP;

import java.util.List;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.ya0ne.core.constants.WSConstants;
import com.ya0ne.core.domain.car.MyCar;
import com.ya0ne.core.domain.cloud.CloudBean;
import com.ya0ne.core.exceptions.CloudException;
import com.ya0ne.core.generated.MyNewCarDto;
import com.ya0ne.core.utilities.http.rest.RESTUtilities;
import com.ya0ne.job.handlers.CurrentCountryCopyHandler;
import com.ya0ne.job.handlers.InitialCopyHandler;
import com.ya0ne.job.utilities.QueueUtilities;

@Aspect
public class CarInCloudAspect extends CloudBean {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CarInCloudAspect.class);
    private MultiValueMap<String, String> parametersMap;
    
    @Autowired private QueueUtilities queueUtilities;
    @Autowired private ApplicationContext context; 

    public CarInCloudAspect() {
    }

    /**
     * Creates new account in the cloud using User Provisioning API
     * @param joinPoint
     * @param myNewCarDto
     * @throws CloudException
     * @throws JMSException 
     */
    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.CarService.addCar(..))",
                    returning = "myNewCarDto")
    public void createCarAccountAfterReturning( JoinPoint joinPoint, Object myNewCarDto ) throws CloudException, JMSException {
        if( myNewCarDto != null  ) {
            parametersMap = new LinkedMultiValueMap<>();
            parametersMap.add("userid", ((MyNewCarDto)myNewCarDto).getVinCode().toUpperCase());
            parametersMap.add("password", ((MyNewCarDto)myNewCarDto).getPassword());
            parametersMap.add("groups[]", DEFAULT_CLOUD_GROUP);
            RESTUtilities.createUser(webCloud + WSConstants.WS_USERS, headers, parametersMap);
            InitialCopyHandler handler = (InitialCopyHandler)context.getBean(InitialCopyHandler.class,
                    new Object[]{((MyNewCarDto)myNewCarDto).getVinCode().toUpperCase(),
                                 ((MyNewCarDto)myNewCarDto).getPassword()});
            queueUtilities.put(handler);
        } else {
            throw new CloudException();
        }
    }

    /**
     * Put a job to copy country and boundaries data
     * @param joinPoint
     * @param carsAndCountriesList
     * @throws CloudException
     * @throws JMSException 
     */
    @AfterReturning(pointcut = "execution(* com.ya0ne.web.service.MyService.loadTimeZones(..))",
                    returning = "carsAndCountriesList")
    @SuppressWarnings("unchecked")
    public void loadGeoTimeZonesAfterReturning( JoinPoint joinPoint, Object[] carsAndCountriesList ) throws CloudException, JMSException {
        if( carsAndCountriesList != null  ) {
            String[] countriesList = (String[])carsAndCountriesList[0];
            List<MyCar> cars = (List<MyCar>)carsAndCountriesList[1];
            cars.forEach(car->{
                try {
                    CurrentCountryCopyHandler handler = (CurrentCountryCopyHandler)context.getBean(CurrentCountryCopyHandler.class,
                        new Object[]{car.getVinCode().toUpperCase(),
                                     car.getPassword(),
                                     countriesList});
                    queueUtilities.put(handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            throw new CloudException();
        }
    }
}
