package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdButton;
import it.arsinfo.smd.vaadin.model.SmdUI;

@SpringUI(path = SmdUI.URL_PROSPETTI)
@Title("Statistiche ADP")
public class ProspettoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    ProspettoDao prospettoDao;
    
    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Statistiche");
        
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();

        SmdButton generaShow = new SmdButton("Genera Prospetti",VaadinIcons.ARCHIVES);
        ProspettoGenera genera = new ProspettoGenera("Genera", VaadinIcons.ENVELOPES,prospettoDao, abbonamentoDao, pubblicazioni);
        ProspettoSearch search = new ProspettoSearch(prospettoDao, pubblicazioni);
        ProspettoGrid grid = new ProspettoGrid("Statistiche");
        
        addSmdComponents(generaShow,genera,search,grid);

        genera.setVisible(false);

        generaShow.setChangeHandler(() -> {
            generaShow.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            genera.edit();
        }); 
        genera.setChangeHandler(() -> {
            generaShow.setVisible(true);
            genera.setVisible(false);
            search.setVisible(true);
            grid.setVisible(true);
        }); 

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(()-> {
            return;
        });
        
        grid.populate(prospettoDao.findAll());

     }

}
