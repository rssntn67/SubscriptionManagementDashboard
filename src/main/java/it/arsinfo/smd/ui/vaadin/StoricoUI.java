package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_STORICO)
@Title("Storico Anagrafica Pubblicazioni ADP")
@Push
public class StoricoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    StoricoDao storicoDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Storico");
        SmdProgressBar pb = new SmdProgressBar();
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        StoricoSearch search = new StoricoSearch(storicoDao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("Storico");
        StoricoEditor editor = 
                new StoricoEditor(
                                  storicoDao, 
                                  abbonamentoDao,
                                  estrattoContoDao,
                                  spedizioneDao,
                                  pubblicazioni, 
                                  anagrafica);
        addSmdComponents(pb,editor,search, grid);
        pb.setVisible(false);
        editor.setVisible(false);
        pb.setChangeHandler(() ->{});
        
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
            search.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            search.setVisible(true);
            setHeader("Storico");
            editor.setVisible(false);
        });

        grid.populate(search.find());

    }
}
