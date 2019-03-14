package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Abbonamento");

        Assert.notNull(abbonamentoDao, "abbonamentoDao must be not null");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(pubblicazioneDao, "pubblicazioneDao must be not null");
        Assert.notNull(spedizioneDao, "spedizioneDao must be not null");
        AbbonamentoAdd add = new AbbonamentoAdd("Aggiungi abbonamento");
        AbbonamentoSearch search = new AbbonamentoSearch(abbonamentoDao,
                                                         anagraficaDao);
        AbbonamentoGrid grid = new AbbonamentoGrid();
        AbbonamentoEditor editor = new AbbonamentoEditor(abbonamentoDao,
                                                         anagraficaDao
                                                         );
        
        SpedizioneGrid spedizioneGrid = new SpedizioneGrid();
        SpedizioneAdd spedizioneAdd = new SpedizioneAdd("Aggiungi spedizione");
        SpedizioneEditor spedizioneEditor = new SpedizioneEditor(spedizioneDao, pubblicazioneDao, anagraficaDao) {
            @Override
            public void save() {
                editor.getRepositoryObj().addSpedizione(getRepositoryObj());
                onChange();
            };
            
            @Override 
            public void delete() {
                editor.getRepositoryObj().deleteSpedizione(getRepositoryObj());
                onChange();
            };
        };

        addSmdComponents(spedizioneEditor,spedizioneAdd,spedizioneGrid,editor, add,search, grid);

        editor.setVisible(false);
        spedizioneEditor.setVisible(false);
        spedizioneAdd.setVisible(false);
        spedizioneGrid.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader(String.format("Abbonamento:new"));
            hideMenu();
            editor.edit(add.generate());
            spedizioneAdd.setAbbonamento(editor.getRepositoryObj());
            spedizioneAdd.setVisible(true);
        });
        
        spedizioneAdd.setChangeHandler(() -> {
            setHeader(String.format("Spedizione:new"));
            hideMenu();
            spedizioneEditor.edit(spedizioneAdd.generate());
            spedizioneAdd.setVisible(true);
        });

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader("Abbonamento:Edit");
            hideMenu();
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
            setHeader("Abbonamento");
            showMenu();
        });

        grid.populate(search.findAll());

    }

}
