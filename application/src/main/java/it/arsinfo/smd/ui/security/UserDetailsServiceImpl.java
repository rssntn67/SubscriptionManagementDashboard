package it.arsinfo.smd.ui.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.entity.UserInfo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    SmdService smdService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
    	UserInfo user = smdService.login(username);
        return new User(user.getUsername(),
        		user.getPasswordHash(),
        		Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
    }
}