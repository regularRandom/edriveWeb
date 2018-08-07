package com.ya0ne.web.security;

import static com.ya0ne.core.constants.Constants.ERR_NOCUSTOMERFOUND;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.ya0ne.core.domain.Account;
import com.ya0ne.core.domain.dao.AccountDAO;
import com.ya0ne.core.domain.service.AccountService;
import com.ya0ne.core.exceptions.DAOException;
import com.ya0ne.core.utilities.http.HttpUtilities;

@Component
public class LocalAuthenticationProvider implements AuthenticationProvider {
    private static Logger logger = Logger.getLogger(LocalAuthenticationProvider.class);
    @Autowired private AccountDAO accountDao;
    @Autowired private HttpUtilities httpUtilities;
    @Autowired private AccountService accountService;

    public LocalAuthenticationProvider() {
        super();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        Account account = accountDao.getAccount( name, password );
        HttpSession session = httpUtilities.getSession();
        if( account != null && account.getId() != ERR_NOCUSTOMERFOUND ) {
            accountService.populateInternalData( account );
            session.setAttribute("account", account);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            try {
                accountService.updateLogonData( account );
            } catch (DAOException e) {
                logger.error("Something wrong with account: " + account.getId() + ", error message is: " + e.getMessage());
                throw new SessionAuthenticationException( e.getMessage() );
            }

            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            SimpleGrantedAuthority sga = new SimpleGrantedAuthority( account.getType().getValue() );
            logger.debug("Granted authority: " + sga.getAuthority());
            grantedAuths.add(sga);
            final UserDetails principal = new User(name, password, grantedAuths);
            return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
        } else {
            logger.error("Username <" + name + "> not found");
            throw new BadCredentialsException("Username not found");
        }
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}
