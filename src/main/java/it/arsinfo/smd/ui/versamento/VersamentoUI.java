package it.arsinfo.smd.ui.versamento;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.CampagnaDao;
import it.arsinfo.smd.dao.VersamentoDao;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.service.SmdService;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonComboBox;

@SpringUI(path = SmdUI.URL_VERSAMENTI)
@Title("Versamenti")
public class VersamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoDao versamentoDao;

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private CampagnaDao campagnaDao;
    
    @Autowired
    private SmdService smdService;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
        AbbonamentoSearch abbSearch = 
        new AbbonamentoSearch(abbonamentoDao,anagraficaDao.findAll(), campagnaDao.findAll());
        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        
        OperazioneIncassoGrid abbonamentiAssociatiGrid = new OperazioneIncassoGrid("Operazioni Incasso Associate");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        SmdButtonComboBox<Anagrafica> associacommittente = 
        		new SmdButtonComboBox<>("Selezionare Committente", 
        				anagraficaDao.findAll(), 
        				"Associa Committente", VaadinIcons.ABACUS);
        associacommittente.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        associacommittente.getButton().setWidth("300px");
        associacommittente.getComboBox().setItemCaptionGenerator(Anagrafica::getCaption);
        associacommittente.getComboBox().setEmptySelectionAllowed(false);
        associacommittente.getComboBox().setWidth("1000px");;
        associacommittente.setVisible(false);
        
        SmdButton dissociacommittente = 
        		new SmdButton("Dissocia Committente", VaadinIcons.BACKWARDS);
        dissociacommittente.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        dissociacommittente.getButton().setWidth("300px");
        dissociacommittente.setVisible(false);

        addSmdComponents(search,grid,associacommittente,dissociacommittente,abbonamentiAssociatiGrid,abbSearch,abbonamentiAssociabiliGrid);
        
        abbSearch.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);
        abbonamentiAssociabiliGrid.setVisible(false);

        grid.getGrid().setHeight("300px");
        abbonamentiAssociabiliGrid.getGrid().setHeight("300px");
        abbonamentiAssociatiGrid.getGrid().setHeight("300px");

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
                abbonamentiAssociatiGrid.populate(smdService.getAssociati(grid.getSelected()));
                abbSearch.setItems(smdService.getAssociabili(grid.getSelected()));
                abbonamentiAssociabiliGrid.populate(abbSearch.find());
                abbSearch.setVisible(true);
                associacommittente.getComboBox().setValue(null);
                if (grid.getSelected().getCommittente() != null) {
                	associacommittente.getComboBox().setValue(anagraficaDao.findById(grid.getSelected().getCommittente().getId()).get());
                	dissociacommittente.setVisible(true);
                }
                associacommittente.setVisible(true);
            } else {
                abbSearch.setVisible(false);
                abbonamentiAssociatiGrid.setVisible(false);
                abbonamentiAssociabiliGrid.setVisible(false);
                associacommittente.setVisible(false);
                dissociacommittente.setVisible(false);
            }
        });
        
        abbonamentiAssociabiliGrid.setChangeHandler(() -> {
        });
        
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
        });
        
        abbonamentiAssociatiGrid.addComponentColumn(operazioneIncasso -> {
            Button button = new Button("Storna");
            button.addClickListener(click -> {
                try {
                    smdService.storna(operazioneIncasso, getLoggedInUser(),"Eseguita da Versamento UI");
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                abbonamentiAssociatiGrid.populate(smdService.getAssociati(grid.getSelected()));
                abbSearch.setItems(smdService.getAssociabili(grid.getSelected()));
                abbonamentiAssociabiliGrid.populate(abbSearch.find());
                abbSearch.setVisible(true);
            });
            
            if (operazioneIncasso.getStatoOperazioneIncasso() != StatoOperazioneIncasso.Incasso) {
                button.setCaption("Non Attivo");
                button.setEnabled(false);
            }
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Incassa");
            button.addClickListener(click -> {
                try {
                    smdService.incassa(abbonamento, grid.getSelected(),getLoggedInUser(),"Eseguita da Versamento UI");
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
               }
                
               abbonamentiAssociatiGrid.populate(smdService.getAssociati(grid.getSelected()));
               abbSearch.reset();
               if (grid.getSelected().getResiduo().signum() > 0) {
                    abbSearch.setItems(smdService.getAssociabili(grid.getSelected()));
                    abbonamentiAssociabiliGrid.populate(abbSearch.find());
                    abbSearch.setVisible(true);
               } else {
                    abbSearch.setVisible(false);
                    abbonamentiAssociabiliGrid.setVisible(false);
               }
            });
            return button;
        });
        
        abbSearch.setChangeHandler(() -> {
            abbonamentiAssociabiliGrid.populate(abbSearch.find());
        });

        dissociacommittente.setChangeHandler(() -> {
			smdService.rimuoviCommittente(grid.getSelected());
			Notification.show("Committente rimosso da Versamento",
                 Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());
        });

        associacommittente.setChangeHandler(() -> {
        	if (associacommittente.getValue() == null ) {
        		return;
        	}
        	smdService.associaCommittente(associacommittente.getValue(),grid.getSelected());				
        	Notification.show("Committente associato a Versamento",
                         Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());
			
        });
        grid.populate(search.findAll());
    }
}
