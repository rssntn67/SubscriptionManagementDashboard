package it.arsinfo.smd.ui.vaadin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.repository.OperazioneDao;

@SpringUI(path = SmdUI.URL_SPEDIZIONERE)
@Title("Ordini Spedizioniere ADP")
@Push
public class SpedizioniereUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private OperazioneDao operazioneDao;

    @Autowired
    private SmdService smdService;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Spedizioniere");
        
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        SpedizioniereItemGrid spedGrid = new SpedizioniereItemGrid("Spedizioni");
        addSmdComponents(spedGrid,grid);
                
        spedGrid.setVisible(false);
        spedGrid.setChangeHandler(() ->{
            spedGrid.setVisible(false);
            grid.populate(operazioneDao.findAll());            
        });
        
        grid.setChangeHandler(()-> {
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("Invia a Spedizioniere",VaadinIcons.ENVELOPES);
            button.setEnabled(op.getStatoOperazione() == StatoOperazione.Inviata);
            button.addClickListener(click -> {
                try {
                    smdService.inviaSpedizionere(op.getMese(),op.getAnno());
                } catch (Exception e) {
                    Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                    return;
                }
                grid.populate(operazioneDao.findAll());
            });
            return button;
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("Visualizza Spedizioni Inviate",VaadinIcons.ENVELOPES);
            button.setEnabled(op.getStatoOperazione() == StatoOperazione.Spedita);
            button.addClickListener(click -> {
                grid.setVisible(false);
                spedGrid.populate(
            		smdService.listItems(
            				op.getPubblicazione(),
            				op.getMese(),
            				op.getAnno(),
            				InvioSpedizione.Spedizioniere,
            				StatoSpedizione.INVIATA));                
            });
            return button;
        });
        
        grid.populate(operazioneDao.findAll());

     }

}
