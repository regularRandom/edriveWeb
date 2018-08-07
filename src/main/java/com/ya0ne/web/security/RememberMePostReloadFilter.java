package com.ya0ne.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ya0ne.core.constants.WSConstants;
import com.ya0ne.core.domain.Account;
import com.ya0ne.core.domain.dao.AccountDAO;
import com.ya0ne.core.domain.service.AccountService;

@Component
public class RememberMePostReloadFilter implements Filter {
    @Autowired AccountService accountService;
    @Autowired AccountDAO accountDao;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String path = request.getRequestURI();
        if( !path.contains(WSConstants.WS_NMEA) ) {
            Account account = (Account)request.getSession().getAttribute("account");
            if( authentication.isAuthenticated() &&
                    request.getUserPrincipal() != null &&
                    account == null ) {
                User user = (User) authentication.getPrincipal();
                account = accountDao.getAccount(user.getUsername());
                accountService.populateInternalData(account);
                request.getSession().setAttribute("account", account);
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                filterConfig.getServletContext());
    }
}
