package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;

@SpringUI(path = SmdUI.URL_SPEDIZIONI)
@Title("Spedizioni ADP")
@Push
public class SpedizioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Spedizioni");
        SmdProgressBar pb = new SmdProgressBar();
        SmdButton bss = new SmdButton("Sospendi Insolventi", VaadinIcons.CLOUD);
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        SpedizioneSearch search = new SpedizioneSearch(spedizioneDao,anagrafica,pubblicazioni);
        SpedizioneGrid grid = new SpedizioneGrid("Spedizioni");
        SpedizioneEditor editor = new SpedizioneEditor(spedizioneDao, pubblicazioni, anagrafica);
        addSmdComponents(pb,bss,editor,search, grid);
        pb.setVisible(false);
        editor.setVisible(false);
        pb.setChangeHandler(() ->{});
        bss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            bss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<Spedizione> aggiornamenti = Smd.spedizioneDaAggiornare(search.find());
                float delta = 1.0f/aggiornamenti.size();
                pb.setValue(0.0f);
                aggiornamenti.stream().forEach(s -> {
                    s.setSospesa(!s.isSospesa());
                    spedizioneDao.save(s);
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
                    setHeader("Spedizione");
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
            setHeader("Spedizione");
            editor.setVisible(false);
        });

        grid.populate(search.find());

    }
}
