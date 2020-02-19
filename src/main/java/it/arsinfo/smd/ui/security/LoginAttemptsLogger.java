package it.arsinfo.smd.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import it.arsinfo.smd.SmdService;

@Component
public class LoginAttemptsLogger {
 
    @Autowired
    private SmdService smdService;

    @EventListener
    public void auditEventHappened(
      AuditApplicationEvent auditApplicationEvent) {
    	smdService.auditlog(auditApplicationEvent);
    }
}