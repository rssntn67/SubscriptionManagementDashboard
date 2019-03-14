package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_VERSAMENTI)
@Title("Abbonamenti ADP")
public class VersamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Autowired
    VersamentoDao versamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");

        Assert.notNull(versamentoDao, "versamentoDao must be not null");
        Assert.notNull(abbonamentoDao, "abbonamentoDao must be not null");

        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("");
        VersamentoEditor editor = new VersamentoEditor(versamentoDao,
                                                       abbonamentoDao);
        addSmdComponents(editor, grid, search);

        editor.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader("Versamenti:Edit");
            hideMenu();

        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
            setHeader("Versamenti");
            showMenu();
        });

        grid.populate(search.findAll());

        grid.populate(search.findAll());

    }

}
