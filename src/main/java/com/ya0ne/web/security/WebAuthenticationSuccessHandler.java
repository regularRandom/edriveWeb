package com.ya0ne.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

/**
* Authentication success handler for Web
*
*/
@Component
public class WebAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static Logger logger = Logger.getLogger(WebAuthenticationSuccessHandler.class);
    private RequestCache requestCache = new HttpSessionRequestCache();
    @Autowired
    private ApplicationContext context;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("Initialization of HyperlinkMapping with customer's data.");
        context.getBean("hyperlinkMapping", HyperlinkMapping.class);

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrl = savedRequest.getRedirectUrl();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        return;
    }
    
    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}