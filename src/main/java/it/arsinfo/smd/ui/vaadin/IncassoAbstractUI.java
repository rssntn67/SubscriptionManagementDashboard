package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;

public abstract class IncassoAbstractUI extends SmdUI {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7864866171039106312L;
    
    @Autowired
    private IncassoDao incassoDao;
    @Autowired    
    private VersamentoDao versamentoDao;
    @Autowired    
    private AbbonamentoDao abbonamentoDao;
    
    protected void dissocia(Abbonamento abbonamento, Versamento versamento) {
        try {
            Incasso incasso = versamento.getIncasso();
            versamentoDao.save(Smd.dissocia(incasso, versamento, abbonamento));
            incassoDao.save(incasso);
            abbonamentoDao.save(abbonamento);
        } catch (UnsupportedOperationException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
            
        }
    }

    protected void incassa(Abbonamento abbonamento, Versamento versamento) {
        try {
            Incasso incasso = versamento.getIncasso();
            versamentoDao.save(Smd.incassa(incasso,versamento, abbonamento));
            incassoDao.save(incasso);
            abbonamentoDao.save(abbonamento);
        } catch (UnsupportedOperationException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } 
    }

    protected List<Abbonamento> getAssociati(Versamento versamento) {
        if (versamento == null) {
            return new ArrayList<>();
        }
        return abbonamentoDao.findByVersamento(versamento);
    }
    
    protected List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().compareTo(BigDecimal.ZERO) <= 0) {
            return new ArrayList<>();
        }
        return abbonamentoDao
                .findByVersamento(null)
                .stream()
                .filter(abb -> abb.getIncassato().equals("No") 
                        && versamento.getResiduo().subtract(abb.getTotale()).compareTo(BigDecimal.ZERO) >= 0
                        && (versamento.getBollettino() != Bollettino.TIPO674) 
                            || abb.getCampo().equals(versamento.getCampo())
                            )
                .collect(Collectors.toList());
    }
    
}
