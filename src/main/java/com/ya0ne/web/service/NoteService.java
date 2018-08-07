package com.ya0ne.web.service;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

public interface NoteService {

    /**
     * Returns page with notes
     * @param locale
     * @return ModelAndView
     */
    ModelAndView getNotes(Locale locale);
}
