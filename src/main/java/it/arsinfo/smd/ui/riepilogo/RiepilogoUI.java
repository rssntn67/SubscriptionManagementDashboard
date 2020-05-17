package it.arsinfo.smd.ui.riepilogo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.OffertaServiceDao;
import it.arsinfo.smd.dao.StoricoServiceDao;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.offerta.OfferteGrid;
import it.arsinfo.smd.ui.storico.StoricoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonTwoComboBox;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;

@SpringUI(path = SmdUI.URL_RIEPILOGO)
@Title(SmdUI.TITLE_RIEPILOGO)
public class RiepilogoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoServiceDao dao;

    @Autowired
    private AbbonamentoServiceDao abbonamentoDao;

    @Autowired
    private StoricoServiceDao storicoDao;

    @Autowired
    private OffertaServiceDao offertaDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, SmdUI.TITLE_RIEPILOGO);
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);
        SmdButtonTwoComboBox<Anagrafica,Anno> search = 
        		new SmdButtonTwoComboBox<>("Selezionare Anagrafica e/o Anno", 
        				anagrafica, Arrays.asList(Anno.values()),
        				"Trova dati", VaadinIcons.ABACUS);
        search.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        search.getButton().setWidth("300px");
        search.getTComboBox().setItemCaptionGenerator(Anagrafica::getCaption);
        search.getTComboBox().setEmptySelectionAllowed(false);
        search.getTComboBox().setWidth("1000px");;
        search.getSComboBox().setItemCaptionGenerator(Anno::getAnnoAsString);
        search.getSComboBox().setEmptySelectionAllowed(false);
        search.getSComboBox().setValue(Anno.getAnnoCorrente());

        StoricoGrid sGrid = new StoricoGrid("Storico");
        AbbonamentoGrid aGrid = new AbbonamentoGrid("Abbonamenti"); 
        VersamentoGrid vGrid = new VersamentoGrid("Versamenti");
        OfferteGrid oGrid = new OfferteGrid("Offerte");

                
        addSmdComponents(search,indietro,sGrid,aGrid,vGrid,oGrid);

        indietro.setVisible(false);
        sGrid.setVisible(false);
        aGrid.setVisible(false);
        vGrid.setVisible(false);
        oGrid.setVisible(false);

        sGrid.setChangeHandler(() -> {});
        aGrid.setChangeHandler(() -> {});
        vGrid.setChangeHandler(() -> {});
        oGrid.setChangeHandler(() -> {});
        
        search.setChangeHandler(() -> {
        	try {
        		sGrid.populate(storicoDao.searchBy(search.getTValue()));
        		aGrid.populate(abbonamentoDao.searchBy(search.getTValue(),search.getSValue()));
        		vGrid.populate(dao.searchBy(search.getTValue(),search.getSValue()));
        		oGrid.populate(offertaDao.findByCommittente(search.getTValue(),search.getSValue()));
        	} catch (Exception e) {
                Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
                return;
			}
        	hideMenu();
        });
                
        indietro.setChangeHandler(() -> {
        	showMenu();
            indietro.setVisible(false);
            sGrid.setVisible(false);
            aGrid.setVisible(false);
            vGrid.setVisible(false);
            oGrid.setVisible(false);
        });
    }
}
