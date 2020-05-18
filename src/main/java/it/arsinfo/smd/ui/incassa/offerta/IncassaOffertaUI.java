package it.arsinfo.smd.ui.incassa.offerta;

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

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.offerta.OfferteGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonTwoComboBox;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;
import it.arsinfo.smd.ui.versamento.VersamentoSearch;

@SpringUI(path = SmdUI.URL_INCASSA_OFFERTA)
@Title(SmdUI.TITLE_INCASSA_OFFERTA)
public class IncassaOffertaUI extends SmdUI {

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
        super.init(request, SmdUI.TITLE_INCASSA_OFFERTA);
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        VersamentoSearch search = new VersamentoSearch(dao,abbonamentoDao.getAnagrafica());
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        grid.getGrid().setHeight("300px");
        
        OfferteGrid offerteGrid = new OfferteGrid("Offerte Associate");
        offerteGrid.getGrid().setHeight("300px");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        SmdButtonTwoComboBox<Anagrafica,Anno> incassaOfferta = 
        		new SmdButtonTwoComboBox<>("Selezionare Anno e Committente", 
        				anagrafica, Arrays.asList(Anno.values()),
        				"Incassa Offerta", VaadinIcons.ABACUS);
        incassaOfferta.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        incassaOfferta.getButton().setWidth("300px");
        incassaOfferta.getTComboBox().setItemCaptionGenerator(Anagrafica::getCaption);
        incassaOfferta.getTComboBox().setEmptySelectionAllowed(false);
        incassaOfferta.getTComboBox().setWidth("1000px");;
        incassaOfferta.getSComboBox().setItemCaptionGenerator(Anno::getAnnoAsString);
        incassaOfferta.getSComboBox().setEmptySelectionAllowed(false);
        incassaOfferta.getSComboBox().setValue(Anno.getAnnoCorrente());
        incassaOfferta.setVisible(false);
                
        addSmdComponents(search,indietro,grid,incassaOfferta,offerteGrid);

        incassaOfferta.setVisible(false);
        indietro.setVisible(false);
        incassaOfferta.setVisible(false);
        offerteGrid.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
            	hideMenu();
            	search.setVisible(false);
            	offerteGrid.populate(dao.getOfferte(grid.getSelected()));
                incassaOfferta.getTComboBox().setValue(null);
                if ( grid.getSelected().getResiduo().signum() > 0) {
    				incassaOfferta.setVisible(true);
                }
                if (grid.getSelected().getCommittente() != null) {
                	incassaOfferta.getTComboBox().setValue(dao.findCommittente(grid.getSelected()));
                }
                indietro.setVisible(true);
            } else {
            	indietro.onChange();
            }
        });
        
        incassaOfferta.setChangeHandler(() -> {
        	try {
				dao.incassa(incassaOfferta.getSValue(), grid.getSelected(), getLoggedInUser(), incassaOfferta.getTValue());
                offerteGrid.populate(dao.getOfferte(grid.getSelected()));
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
            incassaOfferta.setVisible(false);
            indietro.setVisible(false);
            incassaOfferta.setVisible(false);
            offerteGrid.setVisible(false);
        });

        offerteGrid.addComponentColumn(offerta -> {
            Button button = new Button("Storna");
            button.addClickListener(click -> {
                try {
                    dao.storna(offerta, getLoggedInUser());
                    offerteGrid.populate(dao.getOfferte(grid.getSelected()));
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
            });
            
            if (offerta.getStatoOperazioneIncasso() != StatoOperazioneIncasso.Incasso) {
                button.setCaption("Non Attivo");
                button.setEnabled(false);
            }
            return button;
        });
                
        grid.populate(search.find());
    }
}
