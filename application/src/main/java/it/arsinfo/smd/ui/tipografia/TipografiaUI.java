package it.arsinfo.smd.ui.tipografia;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.OperazioneServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.operazione.OperazioneEditor;
import it.arsinfo.smd.ui.operazione.OperazioneGenera;
import it.arsinfo.smd.ui.operazione.OperazioneGrid;
import it.arsinfo.smd.ui.operazione.OperazioneSearch;
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
    private OperazioneServiceDao dao;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService smdService;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Tipografia");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        
        SmdButton generaShow = new SmdButton("Genera Operazioni",VaadinIcons.ARCHIVES);
        OperazioneGenera genera = new OperazioneGenera("Genera", VaadinIcons.ENVELOPES,pubblicazioni);
        OperazioneSearch search = new OperazioneSearch(dao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneEditor editor = new OperazioneEditor(dao.getRepository(), pubblicazioni);
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
            smdService.generaStatisticheTipografia(
            		genera.getAnno(),
            		genera.getMese(),
            		genera.getPubblicazione());
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
        
        grid.populate(search.find());

     }

}
