package it.arsinfo.smd.ui.nota;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.NotaServiceDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_NOTE)
@Title("Note Storico ADP")
public class NotaUI extends SmdEditorUI<Nota> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    NotaServiceDao dao;

    @Override
    protected void init(VaadinRequest request) {
        List<Storico> storici = dao.findStoricoAll();
        NotaSearch search = new NotaSearch(dao,storici);
        NotaAdd add = new NotaAdd("Aggiungi Nota");
        NotaGrid grid = new NotaGrid("Note");
        NotaEditor editor = new NotaEditor(dao, storici);
        init(request,add, search,editor, grid,"Note");
        add.setUser(getLoggedInUser());
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        editor.setVisible(false);
        
        grid.populate(search.findAll());

    }

}
