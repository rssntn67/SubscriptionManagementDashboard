package it.arsinfo.smd.ui.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.ui.vaadin.SmdUI;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        
        log.info("loggedOut user: "+ authentication.getName());
        
        String URL = request.getContextPath() + SmdUI.URL_REDIRECT_LOGOUT;
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(URL);
    }

}
