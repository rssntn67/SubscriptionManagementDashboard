package it.arsinfo.smd.ui.committenti;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonComboBox;
import it.arsinfo.smd.ui.versamento.OperazioneIncassoGrid;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;

@SpringUI(path = SmdUI.URL_VERSAMENTI_COMMITTENTI)
@Title(SmdUI.TITLE_VERSAMENTI_COMMITTENTI)
public class CommittentiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoServiceDao dao;

    @Autowired
    private AbbonamentoServiceDao abbonamentoDao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, SmdUI.TITLE_VERSAMENTI_COMMITTENTI);
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        CommittentiSearch search = new CommittentiSearch(dao,abbonamentoDao.getAnagrafica());
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        
        OperazioneIncassoGrid abbonamentiAssociatiGrid = new OperazioneIncassoGrid("Operazioni Incasso Associate");

        addSmdComponents(search,grid,abbonamentiAssociatiGrid);
        
        abbonamentiAssociatiGrid.setVisible(false);

        grid.getGrid().setHeight("300px");
        abbonamentiAssociatiGrid.getGrid().setHeight("300px");

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

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
                abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
                associacommittente.getComboBox().setValue(null);
                if (grid.getSelected().getCommittente() != null) {
                	associacommittente.getComboBox().setValue(dao.findCommittente(grid.getSelected()));
                	dissociacommittente.setVisible(true);
                }
                associacommittente.setVisible(true);
            } else {
                abbonamentiAssociatiGrid.setVisible(false);
                associacommittente.setVisible(false);
                dissociacommittente.setVisible(false);
            }
        });
                
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
        });
        
        dissociacommittente.setChangeHandler(() -> {
			dao.rimuoviCommittente(grid.getSelected());
			Notification.show("Committente rimosso da Versamento",
                 Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());
        });

        associacommittente.setChangeHandler(() -> {
        	if (associacommittente.getValue() == null ) {
        		return;
        	}
        	dao.associaCommittente(associacommittente.getValue(),grid.getSelected());				
        	Notification.show("Committente associato a Versamento",
                         Notification.Type.HUMANIZED_MESSAGE);
			grid.populate(search.find());
			
        });

                
        grid.populate(search.find());
    }
}
