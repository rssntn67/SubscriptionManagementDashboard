package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Versamento;

public abstract class IncassoAbstractUI extends SmdUI {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7864866171039106312L;
    
    @Autowired
    private SmdService smsService;
    
    protected void dissocia(Abbonamento abbonamento, Versamento versamento) {
        try {
            smsService.reverti(abbonamento, versamento);
        } catch (Exception e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
            
        }
    }

    protected void incassa(Abbonamento abbonamento, Versamento versamento) {
        try {
            smsService.incassa(abbonamento, versamento);
        } catch (Exception e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } 
    }

    protected List<Abbonamento> getAssociati(Versamento versamento) {
        return smsService.getAssociati(versamento);
    }
    
    protected List<Abbonamento> getAssociabili(Versamento versamento) {
        return smsService.getAssociabili(versamento);
    }
    
}
