package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_NOTE)
@Title("Note Storico ADP")
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
