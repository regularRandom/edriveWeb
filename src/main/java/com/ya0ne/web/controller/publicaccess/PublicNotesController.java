package com.ya0ne.web.controller.publicaccess;

import static com.ya0ne.core.constants.WebConstants.WEB_NOTES;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ya0ne.web.service.NoteService;

@Controller
@RequestMapping(value=WEB_NOTES)
public class PublicNotesController {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(PublicNotesController.class);

    @Autowired
    private NoteService noteService;

    /**
     * Start page of my account
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(value="/notes",
                    method = RequestMethod.GET)
    public ModelAndView showNotes( ModelMap model, Locale locale ) {
        return noteService.getNotes( locale );
    }
}
