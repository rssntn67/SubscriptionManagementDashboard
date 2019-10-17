package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_VERSAMENTI)
@Title("Versamenti")
public class VersamentoUI extends IncassoAbstractUI {

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
    
    private static final Logger log = LoggerFactory.getLogger(SmdEditor.class);

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
        AbbonamentoVersamentoSearch abbSearch = 
            new AbbonamentoVersamentoSearch(abbonamentoDao,anagraficaDao.findAll(), campagnaDao.findAll());
        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        VersamentoEditor editor = new VersamentoEditor(versamentoDao) {
            @Override
            public void save() {
                if (get().getImporto().compareTo(BigDecimal.ZERO) <= 0) {
                    Notification.show("Importo non deve essere ZERO",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getDataPagamento().after(get().getDataContabile())) {
                    Notification.show("La data di pagamento deve  essere anteriore alla data contabile",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                try {
                    smdService.save(get());
                    log.info("save: {}", get());
                    onChange();
                } catch (Exception e) {
                    log.warn("save failed for : {}.", get(),e);
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            }
            
            @Override
            public void delete() {
                try {
                    smdService.delete(get());
                    log.info("delete: {}", get());
                    onChange();
                } catch (Exception e) {
                    log.warn("delete failed for : {}.", get(),e);
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            }
        };
        
        AbbonamentoGrid abbonamentiAssociatiGrid = new AbbonamentoGrid("Abbonamenti Associati");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        
        addSmdComponents(search,editor,abbSearch,abbonamentiAssociabiliGrid,abbonamentiAssociatiGrid,grid);
        
        abbSearch.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);
        abbonamentiAssociabiliGrid.setVisible(false);
        editor.setVisible(false);
        abbonamentiAssociabiliGrid.getGrid().setHeight("300px");
        abbonamentiAssociatiGrid.getGrid().setHeight("300px");

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
                editor.edit(grid.getSelected());
                abbonamentiAssociatiGrid.populate(getAssociati(grid.getSelected()));
                List<Abbonamento> associabili = getAssociabili(grid.getSelected());
                abbSearch.setItems(associabili);
                abbonamentiAssociabiliGrid.populate(associabili);
                abbSearch.setVisible(true);
                search.setVisible(false);
                grid.setVisible(false);
            }
        });
        
        editor.setChangeHandler(() -> {
            search.setVisible(true);
            grid.populate(search.find());
            abbSearch.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false);
            abbonamentiAssociabiliGrid.setVisible(false);
            editor.setVisible(false);
        });

        abbonamentiAssociabiliGrid.setChangeHandler(() -> {
        });
        
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
        });
        
        abbonamentiAssociatiGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Dissocia");
            button.addClickListener(click -> {
                dissocia(abbonamento, editor.get());
                editor.edit(versamentoDao.findById(editor.get().getId()).get());
                abbonamentiAssociatiGrid.populate(getAssociati(grid.getSelected()));
                List<Abbonamento> associabili = getAssociabili(grid.getSelected());
                abbSearch.setItems(associabili);
                abbonamentiAssociabiliGrid.populate(associabili);
                });
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Incassa");
            button.addClickListener(click -> {
                incassa(abbonamento, editor.get());
                editor.edit(versamentoDao.findById(editor.get().getId()).get());
                abbonamentiAssociatiGrid.populate(getAssociati(grid.getSelected()));
                List<Abbonamento> associabili = getAssociabili(grid.getSelected());
                abbSearch.setItems(associabili);
                abbonamentiAssociabiliGrid.populate(associabili);
            });
            return button;
        });
        
        abbSearch.setChangeHandler(() -> {
            abbonamentiAssociabiliGrid.populate(abbSearch.find());
        });

        grid.populate(search.findAll());
    }
}
