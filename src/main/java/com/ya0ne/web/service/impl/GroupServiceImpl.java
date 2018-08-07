package com.ya0ne.web.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.ya0ne.core.domain.dao.GroupDAO;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.exceptions.ServiceException;
import com.ya0ne.core.factories.LanguageFactory;
import com.ya0ne.core.generated.CustomGroupDto;
import com.ya0ne.core.generated.DataForDeleteDto;
import com.ya0ne.core.utilities.DomainUtilities;
import com.ya0ne.web.service.GroupService;
import com.ya0ne.core.domain.CustomGroup;
import com.ya0ne.core.domain.converters.GroupConverter;

import static com.ya0ne.core.domain.converters.GroupConverter.toCustomGroupDtos;
import static com.ya0ne.core.utilities.CommonUtilities.toIds;

import java.util.List;
import java.util.Locale;

@Service
public class GroupServiceImpl implements GroupService {
    private final static Logger logger = Logger.getLogger(GroupServiceImpl.class);

    private Gson gson = new Gson();

    @Autowired GroupDAO groupDao;
    @Autowired DomainUtilities domainUtilities;
    @Autowired GroupConverter groupConverter;
    @Autowired LanguageFactory languages;

    @Override
    public ModelAndView getGroups(Locale locale) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("groups", groupDao.get(domainUtilities.getAccount()));
        return mv;
    }

    @Override
    public String getGroupsList() {
        return gson.toJson(toCustomGroupDtos(groupDao.get(domainUtilities.getAccount())));
    }

    @Override
    public Long addCustomGroup(String customGroupDto) throws ServiceException {
        CustomGroup cg = groupConverter.toCustomGroup(gson.fromJson(customGroupDto, CustomGroupDto.class));
        try {
            return groupDao.saveGroup(cg);
        } catch (DAOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Long addToGroup( String dataIds, String groupId ) throws ServiceException {
        try {
            final List<Long> trackIds = toIds(gson.fromJson(dataIds,DataForDeleteDto[].class));
            return groupDao.addTracksToGroup(gson.fromJson(groupId,Long.class), trackIds);
        } catch (DAOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Long clearGroups(String groupIDs) throws ServiceException {
        try {
            final List<Long> groupIds = toIds(gson.fromJson(groupIDs,DataForDeleteDto[].class));
            return groupDao.clearGroups(groupIds);
        } catch (DAOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Long deleteGroups(String groupIDs) throws ServiceException {
        try {
            final List<Long> groupIds = toIds(gson.fromJson(groupIDs,DataForDeleteDto[].class));
            return groupDao.deleteGroups(groupIds);
        } catch (DAOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new ServiceException(e.getMessage());
        }
    }
}
