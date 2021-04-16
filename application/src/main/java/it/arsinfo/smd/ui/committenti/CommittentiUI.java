package it.arsinfo.smd.ui.committenti;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.ui.service.api.AbbonamentoService;
import it.arsinfo.smd.ui.service.api.VersamentoService;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonComboBox;
import it.arsinfo.smd.ui.versamento.OperazioneIncassoGrid;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;
import it.arsinfo.smd.ui.versamento.VersamentoSearch;

@SpringUI(path = SmdUI.URL_VERSAMENTI_COMMITTENTI)
@Title(SmdUI.TITLE_VERSAMENTI_COMMITTENTI)
public class CommittentiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoService dao;

    @Autowired
    private AbbonamentoService abbonamentoDao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, SmdUI.TITLE_VERSAMENTI_COMMITTENTI);
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        VersamentoSearch search = new VersamentoSearch(dao,abbonamentoDao.getAnagrafica());
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        
        OperazioneIncassoGrid abbonamentiAssociatiGrid = new OperazioneIncassoGrid("Operazioni Incasso Associate");

        abbonamentiAssociatiGrid.getGrid().setHeight("300px");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        SmdButtonComboBox<Anagrafica> associacommittente = 
        		new SmdButtonComboBox<>("Selezionare Committente", 
        				anagrafica, 
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
        
        addSmdComponents(search,indietro,grid,associacommittente,dissociacommittente,abbonamentiAssociatiGrid);

        indietro.setVisible(false);

        associacommittente.setVisible(false);
        dissociacommittente.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);


        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
            	hideMenu();
            	search.setVisible(false);
                abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
                associacommittente.getComboBox().setValue(null);
                if (grid.getSelected().getCommittente() != null) {
                	associacommittente.getComboBox().setValue(dao.findCommittente(grid.getSelected()));
                	dissociacommittente.setVisible(true);
                }
                associacommittente.setVisible(true);
                indietro.setVisible(true);
            } else {
            	showMenu();
            	search.setVisible(true);
            	indietro.setVisible(false);
                associacommittente.setVisible(false);
                dissociacommittente.setVisible(false);
                abbonamentiAssociatiGrid.setVisible(false);
            }
        });
                
        indietro.setChangeHandler(() -> {
        	showMenu();
        	search.setVisible(true);
        	grid.populate(search.find());
        	associacommittente.setVisible(true);
        	dissociacommittente.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false);
        	indietro.setVisible(false);
        });

        abbonamentiAssociatiGrid.setChangeHandler(() -> {
        });
        
        dissociacommittente.setChangeHandler(() -> {
			dao.rimuoviCommittente(grid.getSelected());
			Notification.show("Committente rimosso da Versamento",
                 Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());
            abbonamentiAssociatiGrid.setVisible(false);
            associacommittente.setVisible(false);
            dissociacommittente.setVisible(false);
        });

        associacommittente.setChangeHandler(() -> {
        	if (associacommittente.getValue() == null ) {
        		return;
        	}
        	dao.associaCommittente(associacommittente.getValue(),grid.getSelected());				
        	Notification.show("Committente associato a Versamento",
                         Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());			
            abbonamentiAssociatiGrid.setVisible(false);
            associacommittente.setVisible(false);
            dissociacommittente.setVisible(false);
        });
                
        grid.populate(search.find());
    }
}
