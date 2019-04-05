package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_NOTE)
@Title("Note Anagrafica ADP")
public class NotaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    NotaDao notaDao;

    @Autowired
    StoricoDao storicoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Note");
        Assert.notNull(notaDao, "notaDao must be not null");
        Assert.notNull(storicoDao, "storicoDao must be not null");

        List<Storico> storici = storicoDao.findAll();
        NotaSearch search = new NotaSearch(notaDao, storici);
        NotaAdd add = new NotaAdd("Aggiungi Nota");
        NotaGrid grid = new NotaGrid("");
        NotaEditor editor = new NotaEditor(notaDao, storici);
        addSmdComponents(add, search,editor, grid);

        editor.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            search.setVisible(false);
            grid.setVisible(false);
        });

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            search.setVisible(false);
            add.setVisible(false);
            setHeader("Nota:Edit");
            hideMenu();
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
            add.setVisible(true);
            search.setVisible(true);
            setHeader("Note");
            showMenu();
        });

        grid.populate(search.findAll());

    }

}
