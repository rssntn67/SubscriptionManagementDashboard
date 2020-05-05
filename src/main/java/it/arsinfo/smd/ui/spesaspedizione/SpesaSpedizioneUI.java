package it.arsinfo.smd.ui.spesaspedizione;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.SpesaSpedizioneDao;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_SPESESPEDIZIONE)
@Title("Spese di Spedizione")
public class SpesaSpedizioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    SpesaSpedizioneDao spesaSpedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Spese Spedizione");
        
        SpesaSpedizioneAdd add = new SpesaSpedizioneAdd("Aggiungi Spese Spedizione");
        SpesaSpedizioneEditor editor = 
                new SpesaSpedizioneEditor(spesaSpedizioneDao);
        
        SpesaSpedizioneGrid grid = new SpesaSpedizioneGrid("Spese Spedizione");
        
        
        addSmdComponents(editor,add,grid);
        editor.setVisible(false);

        add.setChangeHandler(()-> {
            setHeader(String.format("Spesa Spedizione:Nuova"));
            hideMenu();
            editor.edit(add.generate());
            add.setVisible(false);
            grid.setVisible(false);
        });
                
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            setHeader("Edit:SpesaSpedizione:");
            hideMenu();
            add.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            showMenu();
            add.setVisible(true);
            setHeader("Spese Spedizione");
            editor.setVisible(false);
            grid.populate(spesaSpedizioneDao.findAll());
        });

        grid.populate(spesaSpedizioneDao.findAll());

    }

}
