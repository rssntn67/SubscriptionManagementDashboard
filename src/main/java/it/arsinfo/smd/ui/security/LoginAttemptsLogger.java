package it.arsinfo.smd.ui.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import it.arsinfo.smd.Smd;

@Component
public class LoginAttemptsLogger {
 
    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    @EventListener
    public void auditEventHappened(
      AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
  
 
        WebAuthenticationDetails details
          = (WebAuthenticationDetails) auditEvent.getData().get("details");
        String requestUrl = (String)auditEvent.getData().get("requestUrl");
        if (requestUrl != null && (requestUrl.equals("/") || requestUrl.equals("/login.html?logout"))) {
            return;
        }
        log.info("'{}' {} Remote Ip Address {} SessionId {}" ,
                 auditEvent.getPrincipal() ,
                 auditEvent.getType(),
                 details.getRemoteAddress(),
                 details.getSessionId()
                );
 
    }
}