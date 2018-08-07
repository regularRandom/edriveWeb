package com.ya0ne.web.controller;

import static com.ya0ne.core.constants.WebConstants.WEB_MYGROUPS;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.ya0ne.core.exceptions.ServiceException;
import com.ya0ne.web.service.GroupService;

@Controller
@Secured({"ROLE_USER","ROLE_ADMIN"})
@RequestMapping(WEB_MYGROUPS)
public class GroupController {
    private static Logger logger = Logger.getLogger(TracksController.class);
    private Gson gson = new Gson();

    @Autowired GroupService groupService;

    /**
     * Shows list of groups
     * @param locale
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showTracks( Locale locale ) {
        return groupService.getGroups( locale );
    }

    /**
     * Gets list of customer's groups
     * @param locale
     * @return JSON
     */
    @RequestMapping(value="/getGroupsList",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getGroupsList( Locale locale ) {
        return groupService.getGroupsList();
    }

    @RequestMapping(value="/addCustomGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> addCustomGroup( @RequestParam(value = "group", required = true ) String customGroupDto ) {
        logger.info("CustomGroupDto: " + customGroupDto);
        try {
            return new ResponseEntity<String>(gson.toJson(groupService.addCustomGroup(customGroupDto)),HttpStatus.OK);
        } catch( ServiceException e ) {
            return new ResponseEntity<String>(gson.toJson(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/addToGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> addToGroup( @RequestParam(value = "dataIds", required = true ) String dataIds,
                                                            @RequestParam(value = "group", required = true ) String groupId ) {
        logger.info("Group ID: " + groupId + ", track IDs: " + dataIds);
        try {
            return new ResponseEntity<String>(gson.toJson(groupService.addToGroup(dataIds,groupId)),HttpStatus.OK);
        } catch( ServiceException e ) {
            return new ResponseEntity<String>(gson.toJson(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/clearGroups",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> clearGroups( @RequestParam(value = "dataIds", required = true ) String groupIds ) {
        logger.info("Group IDs to clear: " + groupIds);
        try {
            return new ResponseEntity<String>(gson.toJson(groupService.clearGroups(groupIds)),HttpStatus.OK);
        } catch( ServiceException e ) {
            return new ResponseEntity<String>(gson.toJson(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/deleteGroups",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<String> deleteGroups( @RequestParam(value = "dataIds", required = true ) String groupIds ) {
        logger.info("Group IDs to delete: " + groupIds);
        try {
            return new ResponseEntity<String>(gson.toJson(groupService.deleteGroups(groupIds)),HttpStatus.OK);
        } catch( ServiceException e ) {
            return new ResponseEntity<String>(gson.toJson(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
