package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.VersamentoDao;

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
        
        AbbonamentoVersamentoSearch abbSearch = 
            new AbbonamentoVersamentoSearch(abbonamentoDao,anagraficaDao.findAll(), campagnaDao.findAll());
        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        
        AbbonamentoGrid abbonamentiAssociatiGrid = new AbbonamentoGrid("Abbonamenti Associati");
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
                abbonamentiAssociatiGrid.populate(smdService.getAssociati(grid.getSelected()));
                abbSearch.setItems(smdService.getAssociabili(grid.getSelected()));
                abbonamentiAssociabiliGrid.populate(abbSearch.find());
                abbSearch.setVisible(abbonamentiAssociabiliGrid.getSize() > 0);
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
        
        abbonamentiAssociatiGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Dissocia");
            button.addClickListener(click -> {
                try {
                    smdService.dissocia(abbonamento, grid.getSelected(),getLoggedInUser(),"Eseguita da Versamento UI");
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

        grid.populate(search.findAll());
    }
}
