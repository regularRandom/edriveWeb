package com.ya0ne.web.service;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.core.exceptions.ServiceException;

public interface GroupService {
    /**
     * Shows list of groups
     * @param locale
     * @return ModelAndView
     */
    ModelAndView getGroups( Locale locale );
    
    /**
     * Receives groups list
     * @return JSON string
     */
    String getGroupsList();

    /**
     * Saves new customer's group
     * @param groupDto
     * @return OK
     */
    Long addCustomGroup( String customGroupDto ) throws ServiceException;

    /**
     * Adds entity to group
     * @param dataIds
     * @param groupId
     * @return groupID
     * @throws ServiceException
     */
    Long addToGroup( String dataIds, String groupId ) throws ServiceException;

    /**
     * Clears the selected groups
     * @param groupIDs
     * @return 0 in case of success
     * @throws ServiceException
     */
    Long clearGroups( String groupIDs ) throws ServiceException;

    /**
     * Deletes the selected groups
     * @param groupIDs
     * @return 0 in case of success
     * @throws ServiceException
     */
    Long deleteGroups( String groupIDs ) throws ServiceException;
}
