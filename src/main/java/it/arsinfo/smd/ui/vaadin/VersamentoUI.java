package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.entity.Versamento;
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
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
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

        
        addSmdComponents(search,editor,abbonamentiAssociabiliGrid,abbonamentiAssociatiGrid,grid);
        
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
                abbonamentiAssociabiliGrid.populate(getAssociabili(grid.getSelected()));
                search.setVisible(false);
                grid.setVisible(false);
            }
        });
        
        editor.setChangeHandler(() -> {
            search.setVisible(true);
            grid.populate(search.find());
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
                abbonamentiAssociabiliGrid.populate(getAssociabili(grid.getSelected()));
                });
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Incassa");
            button.addClickListener(click -> {
                incassa(abbonamento, editor.get());
                editor.edit(versamentoDao.findById(editor.get().getId()).get());
                abbonamentiAssociatiGrid.populate(getAssociati(grid.getSelected()));
                abbonamentiAssociabiliGrid.populate(getAssociabili(grid.getSelected()));
            });
            return button;
        });

        grid.populate(search.findAll());
    }
}
