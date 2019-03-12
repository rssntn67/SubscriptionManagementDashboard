package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;
    @Autowired
    PubblicazioneDao pubblicazioneDao;
    @Autowired
    StoricoDao anagraficaPubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(anagraficaPubblicazioneDao,
                       "anagraficaPubblicazioneDao must be not null");
        AnagraficaAdd add = new AnagraficaAdd("Aggiungi ad Anagrafica");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
        AnagraficaGrid grid = new AnagraficaGrid();
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        StoricoByAnagrafica storicoByAnagrafica = new StoricoByAnagrafica(anagraficaPubblicazioneDao);
        StoricoGrid storicoGrid = new StoricoGrid();
        StoricoEditor storicoEditor = 
                new StoricoEditor(
                      anagraficaPubblicazioneDao,
                      pubblicazioneDao,
                      anagraficaDao
        );
        StoricoAdd storicoAdd = new StoricoAdd("Aggiungi Storico");
        addSmdComponents(storicoAdd,storicoEditor, editor, storicoByAnagrafica,storicoGrid, add,search, grid);
        
        editor.setVisible(false);
        storicoByAnagrafica.setVisible(false);
        storicoGrid.setVisible(false);
        storicoEditor.setVisible(false);
        storicoAdd.setVisible(false);

        add.setChangeHandler(() -> {
            setHeader(String.format("Anagrafica:Add"));
            hideMenu();
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(String.format("Anagrafica:Edit:%s", grid.getSelected().getCaption()));
            hideMenu();
            editor.edit(grid.getSelected());
            storicoAdd.setIntestatario(grid.getSelected());
            storicoGrid.populate(storicoByAnagrafica.findByKey(grid.getSelected()));
            storicoAdd.setVisible(true);
        });


        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            editor.setVisible(false);
            storicoGrid.setVisible(false);
            storicoByAnagrafica.setVisible(false);
            storicoAdd.setVisible(false);
            showMenu();
            setHeader("Anagrafica");
        });

        storicoGrid.setChangeHandler(() -> {
            if (storicoGrid.getSelected() == null) {
                return;
            }
            storicoEditor.edit(storicoGrid.getSelected());
            editor.setVisible(false);
            setHeader(String.format("Storico:%s-%s",
                                    storicoGrid.getSelected().getCaptionIntestatario(),
                                    storicoGrid.getSelected().getCaptionPubblicazione()));
        });

        storicoEditor.setChangeHandler(() -> {
            storicoGrid.populate(storicoByAnagrafica.findByKey(grid.getSelected()));
            setHeader(String.format("Anagrafica:Edit:%s", grid.getSelected().getCaption()));
            editor.setVisible(true);
            storicoEditor.setVisible(false);
        });

        storicoAdd.setChangeHandler(() -> {
            storicoEditor.edit(storicoAdd.generate());
            setHeader("Storico:Add");
            editor.setVisible(false);
        });

        grid.populate(search.findAll());

    }

}
