package it.arsinfo.smd.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.repository.UserInfoDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserInfoDao userService;

	@Autowired
	public UserDetailsServiceImpl(UserInfoDao userService) {
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo user = userService.findByUsername(username);
		if (null == user) {
			throw new UsernameNotFoundException("No user present with username: " + username);
		} else {
			return new org.springframework.security.core.userdetails.User(
                      user.getUsername(), 
                      user.getPasswordHash(),
                      Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
		}
	}
}