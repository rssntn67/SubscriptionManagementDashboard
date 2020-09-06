package it.arsinfo.smd.ui.adpsede;

import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.OperazioneServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.tipografia.OperazioneGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdComboBox;

@SpringUI(path = SmdUI.URL_ADPSEDE)
@Title("Ordini ADP Sede")
@Push
public class AdpSedeUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private OperazioneServiceDao serviceDao;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService smdService;

    private InvioSpedizione invio=InvioSpedizione.AdpSede;
    private StatoSpedizione stato=StatoSpedizione.PROGRAMMATA;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Sede");
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();

        AdpSedeSearch search = new AdpSedeSearch(serviceDao, pubblicazioni);
        SmdComboBox<InvioSpedizione> invioSpedizioneComboBox = new SmdComboBox<>("Seleziona", EnumSet.complementOf(EnumSet.of(InvioSpedizione.Spedizioniere)));
        invioSpedizioneComboBox.getComboBox().setValue(invio);
        invioSpedizioneComboBox.getComboBox().setEmptySelectionAllowed(false);
        invioSpedizioneComboBox.getComboBox().setSizeFull();
        
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        AdpSedeEditor editor = new AdpSedeEditor(serviceDao.getRepository(), pubblicazioni);

        SmdComboBox<StatoSpedizione> statoSpedizioneComboBox = new SmdComboBox<>("Seleziona", EnumSet.allOf(StatoSpedizione.class));
        statoSpedizioneComboBox.getComboBox().setValue(stato);
        statoSpedizioneComboBox.getComboBox().setEmptySelectionAllowed(false);
        statoSpedizioneComboBox.getComboBox().setSizeFull();

        AdpSedeItemGrid spedGrid = new AdpSedeItemGrid("Spedizioni Adp Sede");
        
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKWARDS);
        indietro.setVisible(false);
        
        SmdButton spedisci = new SmdButton("Spedisci",VaadinIcons.ENVELOPES);
        spedisci.setVisible(false);
                
        addSmdComponents(search,grid,indietro,editor,invioSpedizioneComboBox,statoSpedizioneComboBox,spedGrid,spedisci);
                    
        search.setChangeHandler(() -> grid.populate(search.find()));
        
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
            	return;
            }
        	indietro.setVisible(true);
        	invioSpedizioneComboBox.setVisible(true);
        	statoSpedizioneComboBox.setVisible(true);
            editor.edit(grid.getSelected());
            grid.setVisible(false);
            search.setVisible(false);
            spedGrid.populate(
        		smdService.listBy(
        				grid.getSelected().getPubblicazione(),
        				grid.getSelected().getMese(),
        				grid.getSelected().getAnno(),
        				stato,
        				invio));
            spedisci.setVisible(true);
            spedisci.getButton().setEnabled(stato == StatoSpedizione.PROGRAMMATA && editor.get().getStatoOperazione() != StatoOperazione.Programmata);
        });

    	indietro.setVisible(false);
        indietro.setChangeHandler(() -> {
        	editor.setVisible(false);
        	spedisci.setVisible(false);
        	spedGrid.setVisible(false);
        	statoSpedizioneComboBox.setVisible(false);
        	invioSpedizioneComboBox.setVisible(false);
        	indietro.setVisible(false);
        	search.setVisible(true);
        	grid.populate(search.find());
        });

        editor.setVisible(false);
        editor.setChangeHandler(()->{});
        
        invioSpedizioneComboBox.setVisible(false);
        invioSpedizioneComboBox.setChangeHandler(() -> {
        	invio = invioSpedizioneComboBox.getValue();
            spedGrid.populate(
        		smdService.listBy(
        				editor.get().getPubblicazione(),
        				editor.get().getMese(),
        				editor.get().getAnno(),
        				stato,
        				invio));
            spedisci.setVisible(true);
            spedisci.getButton().setEnabled(stato == StatoSpedizione.PROGRAMMATA);
        });

        statoSpedizioneComboBox.setVisible(false);
        statoSpedizioneComboBox.setChangeHandler(() -> {
        	stato = statoSpedizioneComboBox.getValue();
            spedGrid.populate(
        		smdService.listBy(
        				editor.get().getPubblicazione(),
        				editor.get().getMese(),
        				editor.get().getAnno(),
        				stato,
        				invio));
            spedisci.setVisible(true);
            spedisci.getButton().setEnabled(stato == StatoSpedizione.PROGRAMMATA);
        });
        
        spedGrid.setVisible(false);
        spedGrid.setChangeHandler(() ->{
        });
                
    	spedisci.setVisible(false);
        spedisci.setChangeHandler(() -> {
        	try {
                smdService.spedisciAdpSede(grid.getSelected().getMese(),grid.getSelected().getAnno(),grid.getSelected().getPubblicazione(),invio);
            	statoSpedizioneComboBox.getComboBox().setValue(StatoSpedizione.INVIATA);
            } catch (Exception e) {
                Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                return;
            }
        });
               
        grid.populate(serviceDao.findAll());

     }

}
