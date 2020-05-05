package it.arsinfo.smd.ui.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import it.arsinfo.smd.service.SmdService;
import it.arsinfo.smd.ui.SmdUI;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private SmdService smdService;
	
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
    	smdService.logout(authentication.getName());
    	String URL = request.getContextPath() + SmdUI.URL_REDIRECT_LOGOUT;
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(URL);
    }

}
