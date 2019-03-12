package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
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
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Abbonamento");

        Assert.notNull(abbonamentoDao, "abbonamentoDao must be not null");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(pubblicazioneDao, "pubblicazioneDao must be not null");
        AbbonamentoSearch search = new AbbonamentoSearch(abbonamentoDao,
                                                         anagraficaDao);
        AbbonamentoGrid grid = new AbbonamentoGrid();
        AbbonamentoEditor editor = new AbbonamentoEditor(abbonamentoDao,
                                                         anagraficaDao,
                                                         pubblicazioneDao);
        addSmdComponents(editor, grid, search);

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
