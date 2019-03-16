package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.NotaDao;
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
    AnagraficaDao anagraficaDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Note");
        Assert.notNull(notaDao, "notaDao must be not null");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");

        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        NotaAdd add = new NotaAdd("Aggiungi Nota",anagrafica.iterator().next());
        NotaSearch search = new NotaSearch(notaDao, anagrafica);
        NotaGrid grid = new NotaGrid("");
        NotaEditor editor = new NotaEditor(notaDao, anagrafica);
        addSmdComponents(add, editor, grid, search);

        editor.setVisible(false);

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader("Nota:Edit");
            hideMenu();
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
            setHeader("Note");
            showMenu();
        });

        grid.populate(search.findAll());

    }

}
