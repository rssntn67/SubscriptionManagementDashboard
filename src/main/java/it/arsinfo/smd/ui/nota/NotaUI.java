package it.arsinfo.smd.ui.nota;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.SmdAbstractUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_NOTE)
@Title("Note Storico ADP")
public class NotaUI extends SmdAbstractUI<Nota> {

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
        List<Storico> storici = storicoDao.findAll();
        NotaSearch search = new NotaSearch(notaDao, storici);
        NotaAdd add = new NotaAdd("Aggiungi Nota");
        NotaGrid grid = new NotaGrid("Note");
        NotaEditor editor = new NotaEditor(notaDao, storici);
        init(request,add, search,editor, grid,"Note");
    }

}
