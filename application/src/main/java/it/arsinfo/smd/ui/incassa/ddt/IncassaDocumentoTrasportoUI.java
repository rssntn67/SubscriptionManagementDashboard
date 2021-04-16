package it.arsinfo.smd.ui.incassa.ddt;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.service.api.VersamentoService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.ddt.DocumentiTrasportoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonTwoComboBoxTextField;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;
import it.arsinfo.smd.ui.versamento.VersamentoSearch;

@SpringUI(path = SmdUI.URL_INCASSA_DDT)
@Title(SmdUI.TITLE_INCASSA_DDT)
public class IncassaDocumentoTrasportoUI extends SmdUI {

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
        super.init(request, SmdUI.TITLE_INCASSA_DDT);
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        VersamentoSearch search = new VersamentoSearch(dao,abbonamentoDao.getAnagrafica());
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        grid.getGrid().setHeight("300px");
        
        DocumentiTrasportoGrid ddtGrid = new DocumentiTrasportoGrid("DDT Associati");
        ddtGrid.getGrid().setHeight("300px");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        SmdButtonTwoComboBoxTextField<Anno,Anagrafica> incassa = 
        		new SmdButtonTwoComboBoxTextField<>("ddt","Importo Anno e Committente", 
        				 Arrays.asList(Anno.values()),anagrafica,
        				"Incassa DDT", VaadinIcons.ABACUS);
        incassa.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        incassa.getButton().setWidth("300px");
        incassa.getSComboBox().setItemCaptionGenerator(Anagrafica::getCaption);
        incassa.getSComboBox().setEmptySelectionAllowed(false);
        incassa.getSComboBox().setWidth("800px");;
        incassa.getTComboBox().setItemCaptionGenerator(Anno::getAnnoAsString);
        incassa.getTComboBox().setEmptySelectionAllowed(false);
        incassa.getTComboBox().setValue(Anno.getAnnoCorrente());
        incassa.setVisible(false);
                
        addSmdComponents(search,indietro,grid,incassa,ddtGrid);

        incassa.setVisible(false);
        indietro.setVisible(false);
        incassa.setVisible(false);
        ddtGrid.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
            	hideMenu();
            	search.setVisible(false);
            	ddtGrid.populate(dao.getDocumentiTrasporto(grid.getSelected()));
                incassa.getTComboBox().setValue(null);
                if ( grid.getSelected().getResiduo().signum() > 0) {
    				incassa.setVisible(true);
                }
                if (grid.getSelected().getCommittente() != null) {
                	incassa.getSComboBox().setValue(dao.findCommittente(grid.getSelected()));
                }
                indietro.setVisible(true);
            } else {
            	indietro.onChange();
            }
        });
        
        incassa.setChangeHandler(() -> {
        	try {
				dao.incassaDdt(incassa.getValueA(),incassa.getValueB(),incassa.getTValue(), grid.getSelected(), getLoggedInUser(), incassa.getSValue());
            	ddtGrid.populate(dao.getDocumentiTrasporto(grid.getSelected()));
			} catch (Exception e) {
                Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
                return;
			}
        });
                
        indietro.setChangeHandler(() -> {
        	showMenu();
        	search.setVisible(true);
            grid.populate(search.find());
            incassa.setVisible(false);
            indietro.setVisible(false);
            incassa.setVisible(false);
            ddtGrid.setVisible(false);
        });

        ddtGrid.addComponentColumn(ddt -> {
            Button button = new Button("Storna");
            button.addClickListener(click -> {
                try {
                    dao.storna(ddt, getLoggedInUser());
                    ddtGrid.populate(dao.getDocumentiTrasporto(grid.getSelected()));
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
            });
            
            if (ddt.getStatoOperazioneIncasso() != StatoOperazioneIncasso.Incasso) {
                button.setCaption("Non Attivo");
                button.setEnabled(false);
            }
            return button;
        });
                
        grid.populate(search.find());
    }
}
