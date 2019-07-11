package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.PubblicazioneDao;

@SpringUI(path = SmdUI.URL_PUBBLICAZIONI)
@Title("Pubblicazioni ADP")
public class PubblicazioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazionedao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Pubblicazioni");
        PubblicazioneAdd add = new PubblicazioneAdd("Aggiungi Pubblicazione");
        PubblicazioneSearch search = new PubblicazioneSearch(pubblicazionedao);
        PubblicazioneGrid grid = new PubblicazioneGrid("Pubblicazioni");
        PubblicazioneEditor editor = new PubblicazioneEditor(pubblicazionedao);
                        
        addSmdComponents(editor,add, search, grid);
        editor.setVisible(false);

        add.setChangeHandler(()-> {
            setHeader(String.format("Pubblicazione:Nuova"));
            hideMenu();
            editor.edit(add.generate());
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
        });
        
        search.setChangeHandler(()-> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            setHeader("Pubblicazioni");
            editor.setVisible(false);
        });

        grid.populate(search.findAll());

    }

}
