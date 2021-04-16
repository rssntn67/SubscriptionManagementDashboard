package it.arsinfo.smd.ui.spesaspedizione;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.SpesaSpedizioneServiceDao;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_SPESESPEDIZIONE)
@Title("Spese di Spedizione")
public class SpesaSpedizioneUI extends SmdEditorUI<SpesaSpedizione> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    SpesaSpedizioneServiceDao spesaSpedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        SpesaSpedizioneSearch search = new SpesaSpedizioneSearch(spesaSpedizioneDao);
        SpesaSpedizioneAdd add = new SpesaSpedizioneAdd("Aggiungi Spese Spedizione");
        SpesaSpedizioneEditor editor = 
                new SpesaSpedizioneEditor(spesaSpedizioneDao);
        
        SpesaSpedizioneGrid grid = new SpesaSpedizioneGrid("Spese Spedizione");
        init(request,add, search,editor, grid, "Spese Spedizione");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        editor.setVisible(false);
        
        grid.populate(search.findAll());

    }

}
