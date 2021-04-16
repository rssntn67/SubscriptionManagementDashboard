package it.arsinfo.smd.ui.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationAuditListener 
  extends AbstractAuthorizationAuditListener {
 
    public static final String AUTHORIZATION_FAILURE 
      = "AUTHORIZATION_FAILURE";

    public static final String LOGGED_OUT 
    = "LOGGED_OUT";

    public static final String REDIRECTED_LOGIN 
    = "REDIRECTED_LOGIN";

    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthorizationFailureEvent) {
            onAuthorizationFailureEvent((AuthorizationFailureEvent) event);
        }
    }
 
    private void onAuthorizationFailureEvent(
      AuthorizationFailureEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put(
          "type", event.getAccessDeniedException().getClass().getName());
        data.put("message", event.getAccessDeniedException().getMessage());
        String requestUrl =((FilterInvocation)event.getSource()).getRequestUrl(); 
        data.put("requestUrl", requestUrl);
        String user = event.getAuthentication().getName();
        if (event.getAuthentication().getDetails() != null) {
            data.put("details", 
              event.getAuthentication().getDetails());
        }
        if ("anonymousUser".equals(user)) {
            publish(new AuditEvent(event.getAuthentication().getName(), 
            		REDIRECTED_LOGIN, data));
        } else {
            publish(new AuditEvent(event.getAuthentication().getName(), 
            		AUTHORIZATION_FAILURE, data));        	
        }
	}
}