package it.arsinfo.smd.ui.versamento;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.SmdService;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoSearch;
import it.arsinfo.smd.ui.vaadin.SmdButtonTextField;

@SpringUI(path = SmdUI.URL_INCASSA_ABB)
@Title("Incassa da Abbonamenti ADP")
public class VersamentoAbbonamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    private AbbonamentoServiceDao dao;
    
    @Autowired
    private SmdService smdService;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Incassa Abbonamento");
        
        List<Anagrafica> anagrafica = dao.getAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.getPubblicazioni();
        List<Campagna> campagne = dao.getCampagne();
        
        AbbonamentoOperazioneIncassoGrid versamentoGrid = new AbbonamentoOperazioneIncassoGrid("Operazioni su Versamenti Associate");
        
        AbbonamentoSearch search = new AbbonamentoSearch(dao,campagne,pubblicazioni,anagrafica);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        VersamentoAbbonamentoEditor editor = new VersamentoAbbonamentoEditor(dao,anagrafica,campagne);

        SmdButtonTextField incassa = new SmdButtonTextField("Inserisci Importo da Incassare","Incassa", VaadinIcons.CASH);
        

        HorizontalLayout lay = new HorizontalLayout();
        lay.addComponents(new Label("     "));
        lay.addComponents(incassa.getComponents());
        addComponents(lay);
        addSmdComponents(versamentoGrid,editor, search, grid);

        editor.setVisible(false);
        incassa.setVisible(false);
        versamentoGrid.setVisible(false);
                
        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            search.setVisible(false);
            editor.edit(grid.getSelected());
            incassa.setVisible(editor.incassare());
            versamentoGrid.populate(smdService.getAssociati(editor.get()));
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            incassa.setVisible(false);
            versamentoGrid.setVisible(false);
            setHeader("Abbonamento");
            showMenu();
            search.setVisible(true);
            grid.populate(search.find());
        });
                
        versamentoGrid.setChangeHandler(() -> {
        	
        });

        incassa.setChangeHandler(() -> {
        	if (incassa.getValue() == null) {
                Notification.show("Devi inserire l'importo da incassare",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
            try {
            	new BigDecimal(incassa.getValue());
            } catch (Exception e) {
                Notification.show("Errore di conversione del valore dell'incasso: " +incassa.getValue(),
                                  Notification.Type.ERROR_MESSAGE);
                return;
            }
        	if (editor.get().getDataContabile() == null) {
                Notification.show("Devi inserire la data contabile",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
        	if (editor.get().getDataPagamento() == null) {
                Notification.show("Devi inserire la data pagamento",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
        	if (editor.get().getProgressivo() == null) {
                Notification.show("Aggiungere Riferimento nel Campo Progressivo",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
            try {
                smdService.incassa(editor.get(), new BigDecimal(incassa.getValue()),getLoggedInUser());
                incassa.setVisible(false);
                editor.edit(editor.get());
                versamentoGrid.populate(smdService.getAssociati(editor.get()));
            } catch (Exception e) {
                Notification.show(e.getMessage(),
                                  Notification.Type.ERROR_MESSAGE);
            }
        });

        grid.populate(search.findAll());

    }

}
