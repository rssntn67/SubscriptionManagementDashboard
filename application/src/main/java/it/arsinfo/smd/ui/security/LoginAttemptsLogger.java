package it.arsinfo.smd.ui.security;

import it.arsinfo.smd.ui.SmdUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptsLogger {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptsLogger.class);

    @EventListener
    public void auditEventHappened(
      AuditApplicationEvent auditApplicationEvent) {

        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();

        WebAuthenticationDetails details
                = (WebAuthenticationDetails) auditEvent.getData().get("details");
        String requestUrl = (String)auditEvent.getData().get("requestUrl");
        if (requestUrl == null && auditEvent.getType().equals("AUTHENTICATION_SUCCESS")) {
            requestUrl = SmdUI.URL_LOGIN;
        } else if (requestUrl == null && auditEvent.getType().equals("AUTHENTICATION_FAILURE")) {
            requestUrl = SmdUI.URL_LOGIN_FAILURE;
        } else if (requestUrl == null) {
            requestUrl="NA";
        }
        String message = (String)auditEvent.getData().get("message");
        String remoteAddress=null;
        String sessionId = null;
        if (details != null) {
            remoteAddress = details.getRemoteAddress();
            if (remoteAddress == null ) {
                remoteAddress = "NA";
            }
            sessionId = details.getSessionId();
            if (sessionId == null) {
                sessionId="NA";
            }
        }
        log.info("auditlog: {} '{} from {}'   URL {}, SessionId {}: {}" ,
                auditEvent.getType(),
                auditEvent.getPrincipal() ,
                remoteAddress,
                requestUrl,
                sessionId,
                message
        );
    }

}