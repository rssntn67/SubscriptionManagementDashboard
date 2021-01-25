package it.arsinfo.smd.ui.adpsede;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.dto.SpedizioneDtoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_ADPSEDE)
@Title("Ordini ADP Sede")
@Push
public class AdpSedeUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService service;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Sede");
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();

        AdpSedeSearch search = new AdpSedeSearch(service, pubblicazioni);
        

        SpedizioneDtoGrid grid = new SpedizioneDtoGrid("Spedizioni Adp Sede");
                
        SmdButton spedisci = new SmdButton("Spedisci",VaadinIcons.ENVELOPES);
                
        addSmdComponents(search,grid,spedisci);
                    
        search.setChangeHandler(() -> {
        	grid.populate(search.find());
            spedisci.setVisible(true);
            spedisci.getButton().setEnabled(search.getStato() == StatoSpedizione.PROGRAMMATA && grid.getSize()>0);
        });
        
        grid.setChangeHandler(()-> {
        });
                
    	spedisci.setVisible(false);
        spedisci.setChangeHandler(() -> {
        	try {
                service.inviaSpedizioni(search.getMese(),search.getAnno(),search.getPubblicazione(),search.getInvio());
            	search.setStato((StatoSpedizione.INVIATA));
            	grid.populate(search.find());
            	spedisci.getButton().setEnabled(false);
            } catch (Exception e) {
                Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                return;
            }
        });
               
        grid.populate(search.find());

     }

}
