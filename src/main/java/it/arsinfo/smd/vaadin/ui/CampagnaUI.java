package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_CAMPAGNA)
@Title("Campagna Abbonamenti ADP")
public class CampagnaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    CampagnaDao campagnaDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Campagna");
        Assert.notNull(campagnaDao, "campagnaDao must be not null");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(pubblicazioneDao, "pubblicazioneDao must be not null");
        CampagnaSearch search = new CampagnaSearch(campagnaDao);
        CampagnaGrid grid = new CampagnaGrid("");
        CampagnaEditor editor = new CampagnaEditor(campagnaDao, anagraficaDao,
                                                   pubblicazioneDao);
        addSmdComponents(editor, search, grid);

        editor.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader("Campagna:Edit");
            hideMenu();

        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
            setHeader("Campagna");
            showMenu();
        });
        grid.populate(search.findAll());

    }

}
