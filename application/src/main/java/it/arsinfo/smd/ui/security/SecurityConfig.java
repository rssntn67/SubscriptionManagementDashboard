package it.arsinfo.smd.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.ui.SmdUI;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	private final PasswordEncoder passwordEncoder;

	private final RedirectAuthenticationSuccessHandler successHandler;
	private final LogoutSuccessHandler logoutHandler;
	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService, 
			PasswordEncoder passwordEncoder,
			RedirectAuthenticationSuccessHandler successHandler, 
			LogoutSuccessHandler logouthandler) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.successHandler = successHandler;
		this.logoutHandler=logouthandler;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry reg = http
				.authorizeRequests();

		reg = reg.antMatchers("/adp.png").permitAll();
		reg = reg.antMatchers("/favicon.ico").permitAll();
		reg = reg.antMatchers("/VAADIN/**").permitAll();
		
		reg = reg.antMatchers(SmdUI.URL_ADMIN_USER).hasAnyAuthority(Role.ADMIN.name());
		reg = reg.antMatchers(SmdUI.URL_RESET_PASS).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name());
		reg = reg.antMatchers("/**").hasAnyAuthority(UserInfo.getRoleNames());
		HttpSecurity sec = reg.and();

		// Allow access to login page without login
		FormLoginConfigurer<HttpSecurity> login = sec.formLogin().permitAll();
		login = login.loginPage(SmdUI.URL_LOGIN).loginProcessingUrl(SmdUI.URL_LOGIN_PROCESSING)
				.failureUrl(SmdUI.URL_LOGIN_FAILURE).successHandler(successHandler);
		login.and().logout().logoutSuccessHandler(logoutHandler)
				.invalidateHttpSession(true).deleteCookies("JSESSIONID")                ;
	}
	
}
