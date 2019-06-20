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
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.EstrattoContoDao;

@SpringUI(path = SmdUI.URL_ESTRATTO_CONTO)
@Title("Estratto Conto")
@Push
public class EstrattoContoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    EstrattoContoDao estrattoContoDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Estratto Conto");
        SmdProgressBar pb = new SmdProgressBar();
        SmdButton bss = new SmdButton("Sospendi Insolventi", VaadinIcons.CLOUD);
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        EstrattoContoSearch search = new EstrattoContoSearch(estrattoContoDao,anagrafica,pubblicazioni);
        EstrattoContoGrid grid = new EstrattoContoGrid("Spedizioni");
        EstrattoContoEditor editor = new EstrattoContoEditor(estrattoContoDao, pubblicazioni, anagrafica);
        addSmdComponents(pb,bss,editor,search, grid);
        pb.setVisible(false);
        editor.setVisible(false);
        pb.setChangeHandler(() ->{});
        bss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            bss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<EstrattoConto> aggiornamenti = Smd.estrattiContoDaAggiornare(search.find());
                float delta = 1.0f/aggiornamenti.size();
                pb.setValue(0.0f);
                aggiornamenti.stream().forEach(s -> {
                    s.setSospesa(!s.isSospesa());
                    estrattoContoDao.save(s);
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
