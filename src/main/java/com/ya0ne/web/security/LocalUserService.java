package com.ya0ne.web.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ya0ne.core.domain.Account;
import com.ya0ne.core.domain.dao.AccountDAO;

@Service
public class LocalUserService implements UserDetailsService {

    @Autowired private AccountDAO accountDao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountDao.getAccount(username);
        if( account == null ) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDetails userDetails = new User(account.getName(),account.getPassword(),Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority(account.getType().getValue()) }));
        return userDetails;
    }
}
