package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.entity.Versamento;
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
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
        AbbonamentoVersamentoSearch abbSearch = 
            new AbbonamentoVersamentoSearch(abbonamentoDao,anagraficaDao.findAll(), campagnaDao.findAll());
        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("Versamenti");
        VersamentoEditor editor = new VersamentoEditor(versamentoDao) {
            @Override
            public void focus(boolean persisted, Versamento versamento) {
                super.focus(persisted, versamento);
                getDelete().setVisible(false);
                getSave().setVisible(false);
                getCancel().setVisible(false);
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
