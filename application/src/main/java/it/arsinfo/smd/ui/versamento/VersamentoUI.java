package it.arsinfo.smd.ui.versamento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.service.api.VersamentoService;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;

@SpringUI(path = SmdUI.URL_INCASSA_VERSAMENTI)
@Title("Versamenti")
public class VersamentoUI extends SmdUI {

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
        super.init(request, "Incasso Versamenti");
        
        List<Anagrafica> anagrafica = abbonamentoDao.getAnagrafica();
        List<Campagna> campagne = abbonamentoDao.getCampagne();
        VersamentoAbbonamentoSearch abbSearch = 
        		new VersamentoAbbonamentoSearch(abbonamentoDao,anagrafica, campagne);
        VersamentoSearch search = new VersamentoSearch(dao,anagrafica);
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        
        
        OperazioneIncassoGrid abbonamentiAssociatiGrid = new OperazioneIncassoGrid("Operazioni Incasso Associate");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        addSmdComponents(search,grid,abbonamentiAssociatiGrid,abbSearch,abbonamentiAssociabiliGrid);
        
        abbSearch.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);
        abbonamentiAssociabiliGrid.setVisible(false);

        grid.getGrid().setHeight("300px");
        abbonamentiAssociabiliGrid.getGrid().setHeight("300px");
        abbonamentiAssociatiGrid.getGrid().setHeight("300px");

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
                abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
                abbSearch.setItems(dao.getAssociabili(grid.getSelected()));
                abbonamentiAssociabiliGrid.populate(abbSearch.find());
                abbSearch.setVisible(true);
            } else {
                abbSearch.setVisible(false);
                abbonamentiAssociatiGrid.setVisible(false);
                abbonamentiAssociabiliGrid.setVisible(false);
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
                    dao.storna(operazioneIncasso, getLoggedInUser(),"Eseguita da Versamento UI");
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
                abbSearch.setItems(dao.getAssociabili(grid.getSelected()));
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
                    dao.incassa(abbonamento, grid.getSelected(),getLoggedInUser(),"Eseguita da Versamento UI");
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
               }
                
               abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
               abbSearch.reset();
               if (grid.getSelected().getResiduo().signum() > 0) {
                    abbSearch.setItems(dao.getAssociabili(grid.getSelected()));
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

        grid.populate(search.findAll());
    }
}
