package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
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

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Storico");
        SmdProgressBar pb = new SmdProgressBar();
        SmdButton bss = new SmdButton("Aggiorna Stato Storici", VaadinIcons.CLOUD);
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        StoricoSearch search = new StoricoSearch(storicoDao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("");
        StoricoEditor editor = new StoricoEditor(storicoDao, pubblicazioni, anagrafica);
        addSmdComponents(pb,bss,editor,search, grid);
        pb.setVisible(false);
        editor.setVisible(false);
        pb.setChangeHandler(() ->{});
        bss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            bss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<Abbonamento> abbonamenti = abbonamentoDao.findByAnno(Smd.getAnnoCorrente());
                List<Storico> storici = search.find();
                float delta = 1.0f/storici.size();
                pb.setValue(0.0f);
                storici.stream().forEach( s -> {
                    StatoStorico calcolato =  Smd.getStatoStorico(s, abbonamenti);
                    if (s.getStatoStorico() != calcolato) {
                        s.setStatoStorico(calcolato);
                        storicoDao.save(s);
                    }
                    access(() -> {
                        pb.setValue(pb.getValue()+delta);
                        grid.populate(search.find());
                        this.push();
                    });
                });

                access(() -> {
                    pb.setValue(0.0f);
                    pb.setVisible(false);
                    bss.getButton().setEnabled(true);
                    setHeader("Storico");
                    grid.populate(search.find());
                    this.push();
                });

            }).start();            
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
            search.setVisible(false);
            bss.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            search.setVisible(true);
            bss.setVisible(true);
            setHeader("Storico");
            editor.setVisible(false);
        });

        grid.populate(search.find());

    }
}
