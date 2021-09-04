package it.arsinfo.smd.ui.riepilogo;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.service.api.OffertaService;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.service.api.VersamentoService;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.offerta.OfferteGrid;
import it.arsinfo.smd.ui.storico.StoricoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdComboBox;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;

@SpringUI(path = SmdUI.URL_RIEPILOGO)
@Title(SmdUI.TITLE_RIEPILOGO)
public class RiepilogoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoService dao;

    @Autowired
    private AbbonamentoService abbonamentoDao;

    @Autowired
    private StoricoService storicoDao;

    @Autowired
    private OffertaService offertaDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, SmdUI.TITLE_RIEPILOGO);
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);
        SmdComboBox<Anagrafica> who = new SmdComboBox<Anagrafica>("Seleziona Anagrafica",abbonamentoDao.getAnagrafica());
        who.getComboBox().setItemCaptionGenerator(Anagrafica::getCaption);
        who.getComboBox().setEmptySelectionAllowed(false);
        SmdComboBox<Anno> when = new SmdComboBox<Anno>("Seleziona Anno",EnumSet.allOf(Anno.class));
        when.getComboBox().setItemCaptionGenerator(Anno::getAnnoAsString);
        when.getComboBox().setEmptySelectionAllowed(false);
        when.getComboBox().setValue(Anno.getAnnoCorrente());
        
        HorizontalLayout search = new HorizontalLayout(when.getComponents());
        search.addComponentsAndExpand(who.getComponents());
                
        StoricoGrid sGrid = new StoricoGrid("Storico");
        AbbonamentoGrid aGrid = new AbbonamentoGrid("Abbonamenti"); 
        VersamentoGrid vGrid = new VersamentoGrid("Versamenti");
        OfferteGrid oGrid = new OfferteGrid("Offerte");

               
        sGrid.setChangeHandler(() -> {});
        aGrid.setChangeHandler(() -> {});
        vGrid.setChangeHandler(() -> {});
        oGrid.setChangeHandler(() -> {});
        
        when.setChangeHandler(() -> {
        	if (who.getValue() == null) {
                Notification.show("Sciegliere Anagrafica",
                        Notification.Type.ERROR_MESSAGE); 
                return;
        	}
        	try {
        		setHeader("Riepilogo:" + who.getValue().getHeader());
        		sGrid.populate(storicoDao.searchBy(who.getValue()));
        		aGrid.populate(abbonamentoDao.searchBy(who.getValue(),when.getValue()));
        		vGrid.populate(dao.searchBy(who.getValue(),when.getValue()));
        		oGrid.populate(offertaDao.findByCommittente(who.getValue(),when.getValue()));
        		indietro.setVisible(true);
        	} catch (Exception e) {
                Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
                return;
			}
        	hideMenu();
        });

        who.setChangeHandler(() -> {
        	try {
        		setHeader("Riepilogo:" + who.getValue().getHeader());
        		sGrid.populate(storicoDao.searchBy(who.getValue()));
        		aGrid.populate(abbonamentoDao.searchBy(who.getValue(),when.getValue()));
        		vGrid.populate(dao.searchBy(who.getValue(),when.getValue()));
        		oGrid.populate(offertaDao.findByCommittente(who.getValue(),when.getValue()));
        		indietro.setVisible(true);
        	} catch (Exception e) {
                Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
                return;
			}
        	hideMenu();
        });

        indietro.setChangeHandler(() -> {
        	showMenu();
        	setHeader(TITLE_RIEPILOGO);
            indietro.setVisible(false);
            sGrid.setVisible(false);
            aGrid.setVisible(false);
            vGrid.setVisible(false);
            oGrid.setVisible(false);
        });
    
        addComponents(search);
        addSmdComponents(indietro,sGrid,aGrid,vGrid,oGrid);

        indietro.setVisible(false);
        sGrid.setVisible(false);
        aGrid.setVisible(false);
        vGrid.setVisible(false);
        oGrid.setVisible(false);

    }
}
