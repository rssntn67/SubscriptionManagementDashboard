package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;

@SpringUI(path = SmdUI.URL_ANAGRAFICA)
@Title("Anagrafica ADP")
public class AnagraficaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        AnagraficaAdd add = new AnagraficaAdd("Aggiungi ad Anagrafica");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
        AnagraficaGrid grid = new AnagraficaGrid("Anagrafiche");
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        
        
        addSmdComponents(
                         editor, 
                         add,
                         search, 
                         grid);

        editor.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Anagrafica:Nuova");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            editor.setVisible(false);
            setHeader("Anagrafica");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
        });

        grid.populate(search.findAll());

    }
    
}
