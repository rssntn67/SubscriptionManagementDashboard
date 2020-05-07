package it.arsinfo.smd.ui.tipografia;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.SmdService;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_TIPOGRAFIA)
@Title("Ordini Tipografia ADP")
@Push
public class TipografiaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private OperazioneDao operazioneDao;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService smdService;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Tipografia");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        
        SmdButton generaShow = new SmdButton("Genera Operazioni",VaadinIcons.ARCHIVES);
        OperazioneGenera genera = new OperazioneGenera("Genera", VaadinIcons.ENVELOPES);
        OperazioneSearch search = new OperazioneSearch(operazioneDao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneEditor editor = new OperazioneEditor(operazioneDao, pubblicazioni);
        addSmdComponents(generaShow,genera,editor,search,grid);
        
        
        genera.setVisible(false);
        editor.setVisible(false);
         
        generaShow.setChangeHandler(() -> {
            generaShow.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            genera.setVisible(true);
        }); 
        genera.setChangeHandler(() -> {
            if (genera.isGenera()) {
                smdService.generaStatisticheTipografia(Anno.getAnnoCorrente(), Mese.getMeseCorrente());
            } else if (genera.isGeneraA()) {
               smdService.generaStatisticheTipografia(genera.getAnno());
            }
            generaShow.setVisible(true);
            genera.setVisible(false);
            search.setVisible(true);
            grid.populate(search.find());
        }); 
        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            generaShow.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());   
        });
        
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            generaShow.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());;            
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("invia a tipografia",VaadinIcons.ENVELOPES);
            button.setEnabled(
                  op.getStatoOperazione() == StatoOperazione.Programmata 
              && op.getStimatoSede() <= op.getDefinitivoSede() 
              && op.getStimatoSped() <= op.getDefinitivoSped()
              );
            button.addClickListener(click -> {
                op.setStatoOperazione(StatoOperazione.Inviata);
                operazioneDao.save(op);
                grid.populate(search.find());
            });
            return button;
        });

        grid.populate(operazioneDao.findAll());

     }

}
