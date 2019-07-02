package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;

@SpringUI(path = SmdUI.URL_PUBBLICAZIONI)
@Title("Pubblicazioni ADP")
public class PubblicazioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazionedao;

    @Autowired
    SpesaSpedizioneDao spesaSpedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Pubblicazioni");
        PubblicazioneAdd add = new PubblicazioneAdd("Aggiungi Pubblicazione");
        PubblicazioneSearch search = new PubblicazioneSearch(pubblicazionedao);
        PubblicazioneGrid grid = new PubblicazioneGrid("Pubblicazioni");
        PubblicazioneEditor editor = new PubblicazioneEditor(pubblicazionedao);
        
        SpesaSpedizioneAdd speseSpedAdd = new SpesaSpedizioneAdd("Aggiungi Spese Spedizione");
        SpesaSpedizioneEditor speseSpedEditor = 
                new SpesaSpedizioneEditor(spesaSpedizioneDao, pubblicazionedao.findAll());
        
        SpesaSpedizioneGrid speseSpedGrid = new SpesaSpedizioneGrid("Spese Spedizione");
        
        
        addSmdComponents(speseSpedAdd,editor,speseSpedEditor,speseSpedGrid,add, search, grid);
        speseSpedAdd.setVisible(false);
        editor.setVisible(false);
        speseSpedEditor.setVisible(false);
        speseSpedGrid.setVisible(false);

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
            speseSpedAdd.setPubblicazione(grid.getSelected());
            speseSpedAdd.setVisible(true);
            speseSpedGrid.populate(spesaSpedizioneDao.findByPubblicazione(grid.getSelected()));
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            setHeader("Pubblicazioni");
            editor.setVisible(false);
            speseSpedGrid.setVisible(false);
            speseSpedAdd.setVisible(false);
        });

        speseSpedAdd.setChangeHandler(() -> {
            speseSpedEditor.edit(speseSpedAdd.generate());
            setHeader(String.format("%s:SpesaSpedizione:Nuova",editor.get().getHeader()));
            speseSpedAdd.setVisible(false);
            editor.setVisible(false);
        });
        
        speseSpedGrid.setChangeHandler(() -> {
            if (speseSpedGrid.getSelected() == null) {
                return;
            }
            setHeader("Edit:SpesaSpedizione");
            speseSpedEditor.edit(speseSpedGrid.getSelected());
            add.setVisible(false);
            search.setVisible(false);
            editor.setVisible(false);
            speseSpedAdd.setVisible(false);
        });
        
        speseSpedEditor.setChangeHandler(() -> {
            speseSpedGrid.populate((spesaSpedizioneDao.findByPubblicazione(grid.getSelected())));
            setHeader(grid.getSelected().getHeader());
            editor.setVisible(true);
            speseSpedEditor.setVisible(false);
            speseSpedAdd.setVisible(true);
        });
        grid.populate(search.findAll());

    }

}
