package it.arsinfo.smd.ui.adpsede;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.entity.StatoSpedizione;
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
    private SmdService service;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Sede");

        AdpSedeSearch search = new AdpSedeSearch(service);
        

        SpedizioneDtoGrid grid = new SpedizioneDtoGrid("Spedizioni Adp Sede");
                
        SmdButton spedisci = new SmdButton("Spedisci",VaadinIcons.ENVELOPES);
        SmdButton annulla = new SmdButton("Annulla",VaadinIcons.UMBRELLA);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(spedisci.getComponents());
        buttons.addComponents(annulla.getComponents());
        addSmdComponents(search,grid);
        addComponents(buttons);
        search.setChangeHandler(() -> {
        	grid.populate(search.find());
            spedisci.getButton().setEnabled(search.getStato() == StatoSpedizione.PROGRAMMATA && grid.getSize()>0);
            annulla.getButton().setEnabled(search.getStato() == StatoSpedizione.SOSPESA && grid.getSize()>0);
        });
        
        grid.setChangeHandler(()-> {
        });
                
        spedisci.setChangeHandler(() -> {
        	try {
                service.inviaAdpSede(search.getMese(),search.getAnno(),search.getInvio());
            	search.setStato((StatoSpedizione.INVIATA));
            	grid.populate(search.find());
            	spedisci.getButton().setEnabled(false);
            	annulla.getButton().setEnabled(false);
            } catch (Exception e) {
                Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                return;
            }
        });

        annulla.setChangeHandler(() -> {
        	try {
                service.annullaAdpSede(search.getMese(),search.getAnno(),search.getInvio());
            	search.setStato((StatoSpedizione.ANNULLATA));
            	grid.populate(search.find());
            	annulla.getButton().setEnabled(false);
            	spedisci.getButton().setEnabled(false);
            } catch (Exception e) {
                Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                return;
            }
        });

        grid.populate(search.searchDefault());
        spedisci.getButton().setEnabled(search.getStato() == StatoSpedizione.PROGRAMMATA && grid.getSize()>0);
        annulla.getButton().setEnabled(search.getStato() == StatoSpedizione.SOSPESA && grid.getSize()>0);

     }

}
