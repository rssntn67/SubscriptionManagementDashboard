package it.arsinfo.smd.ui.spedizioniere;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.service.api.OperazioneService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.dao.PubblicazioneDao;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.dto.SpedizioneDtoGrid;
import it.arsinfo.smd.ui.operazione.OperazioneGrid;
import it.arsinfo.smd.ui.operazione.OperazioneReadOnlyEditor;
import it.arsinfo.smd.ui.operazione.OperazioneSearch;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_SPEDIZIONERE)
@Title("Ordini Spedizioniere ADP")
@Push
public class SpedizioniereUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;
    @Autowired
    private OperazioneService dao;

    @Autowired
    private SmdService smdService;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Spedizioniere");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        OperazioneSearch search = new OperazioneSearch(dao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneReadOnlyEditor editor = new OperazioneReadOnlyEditor(dao.getRepository(), pubblicazioni);
        SpedizioneDtoGrid spedGrid = new SpedizioneDtoGrid("Spedizioni");
        SmdButton spedisci = new SmdButton("Invia a Spedizioniere", VaadinIcons.ENVELOPES);
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKWARDS);
        SmdButton visualiz = new SmdButton("Visualizza Spedizioni",VaadinIcons.ENVELOPES);
        addSmdComponents(search,grid,indietro,editor,spedisci,visualiz,spedGrid);
        
        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
            	return;
            }
        	indietro.setVisible(true);
            editor.edit(grid.getSelected());
            grid.setVisible(false);
            search.setVisible(false);
            spedisci.setVisible(grid.getSelected().getStatoOperazione() == StatoOperazione.Inviata);
            visualiz.setVisible(grid.getSelected().getStatoOperazione() == StatoOperazione.Spedita);
        });

        editor.setVisible(false);
        editor.setChangeHandler(()->{});

        spedGrid.setVisible(false);
        spedGrid.setChangeHandler(() ->{
        });

    	indietro.setVisible(false);
        indietro.setChangeHandler(() -> {
        	editor.setVisible(false);
        	spedisci.setVisible(false);
        	visualiz.setVisible(false);
        	spedGrid.setVisible(false);
        	indietro.setVisible(false);
        	search.setVisible(true);
        	grid.populate(search.find());
        });
        
        spedisci.setVisible(false);
        spedisci.setChangeHandler(() -> {
        	try {
                smdService.inviaSpedizionere(editor.get());
            } catch (Exception e) {
                Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                return;
            }
        	editor.setVisible(false);
        	spedisci.setVisible(false);
        	visualiz.setVisible(false);
        	spedGrid.setVisible(false);
        	indietro.setVisible(false);
        	search.setVisible(true);
        	grid.populate(search.find());
        });
        
        visualiz.setVisible(false);
        visualiz.setChangeHandler(() -> {
            spedGrid.populate(
            		smdService.listBy(
            				editor.get().getPubblicazione(),
            				editor.get().getMese(),
            				editor.get().getAnno(),
            				StatoSpedizione.INVIATA,
            				InvioSpedizione.Spedizioniere));                
        });
        
        grid.populate(search.searchDefault());

     }

}
