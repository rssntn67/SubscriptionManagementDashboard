package it.arsinfo.smd.ui.spedizione;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.PubblicazioneDao;
import it.arsinfo.smd.dao.SpedizioneDao;
import it.arsinfo.smd.dao.SpedizioneItemDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.SmdService;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_SPEDIZIONI)
@Title("Spedizioni")
@Push
public class SpedizioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    SpedizioneDao spedizioneDao;
    
    @Autowired
    SpedizioneItemDao spedizioneItemDao;
    
    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    PubblicazioneDao pubblicazioneDao; 
    
    @Autowired
    SmdService smdService;
    

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Spedizioni");
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Abbonamento> abbonamenti = abbonamentoDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        SpedizioneSearch search = new SpedizioneSearch(spedizioneDao,abbonamenti,anagrafica,pubblicazioni);
        search.setSmdService(smdService);
        SpedizioneGrid grid = new SpedizioneGrid("Spedizioni");
        SpedizioneEditor editor = new SpedizioneEditor(spedizioneDao, anagrafica);
        editor.setSmdService(smdService);
        SpedizioneItemGrid itemgrid = new SpedizioneItemGrid("Items");
        addSmdComponents(editor,itemgrid,search, grid);
        editor.setVisible(false);
        itemgrid.setVisible(false);
        
        search.setChangeHandler(()-> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            editor.edit(grid.getSelected());
            itemgrid.populate(spedizioneItemDao.findBySpedizione(editor.get()));
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            search.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            search.setVisible(true);
            setHeader("Spedizioni");
            editor.setVisible(false);
            itemgrid.setVisible(false);
        });

        itemgrid.setChangeHandler(() -> {
            
        });
        grid.populate(search.find());

    }
}
