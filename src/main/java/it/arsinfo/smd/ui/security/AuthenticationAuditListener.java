package it.arsinfo.smd.ui.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAuditListener 
  extends AbstractAuthenticationAuditListener {
 
    public static final String AUTHENTICATION_FAILURE 
      = "AUTHENTICATION_FAILURE";

    public static final String AUTHENTICATION_SUCCESS 
    = "AUTHENTICATION_SUCCESS";

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            onAuthenticationFailureEvent((AuthenticationFailureBadCredentialsEvent) event);
        }
        if (event instanceof AuthenticationSuccessEvent) {
            onAuthenticationSuccessEvent((AuthenticationSuccessEvent) event);
        }

    }

    private void onAuthenticationSuccessEvent(
    		AuthenticationSuccessEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put(
          "type", AuthenticationSuccessEvent.class.getName());
        data.put("message", "authenticated: " + event.getAuthentication().getName());
         
        if (event.getAuthentication().getDetails() != null) {
            data.put("details", 
              event.getAuthentication().getDetails());
        }
        publish(new AuditEvent(event.getAuthentication().getName(), 
          AUTHENTICATION_SUCCESS, data));
    }

    private void onAuthenticationFailureEvent(
    		AuthenticationFailureBadCredentialsEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put(
          "type", AuthenticationFailureBadCredentialsEvent.class.getName());
        data.put("message", event.getException().getMessage());
         
        if (event.getAuthentication().getDetails() != null) {
            data.put("details", 
              event.getAuthentication().getDetails());
        }
        publish(new AuditEvent(event.getAuthentication().getName(), 
          AUTHENTICATION_FAILURE, data));
    }
}