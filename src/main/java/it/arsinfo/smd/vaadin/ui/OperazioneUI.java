package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdButton;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_OPERAZIONI)
@Title("Operazioni ADP")
public class OperazioneUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    OperazioneDao operazioneDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Operazioni");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        
        SmdButton insolventi = new SmdButton("Insolventi", VaadinIcons.ENVELOPES);
        SmdButton generaShow = new SmdButton("Genera Operazioni",VaadinIcons.ARCHIVES);
        OperazioneGenera genera = new OperazioneGenera("Genera", VaadinIcons.ENVELOPES,operazioneDao, abbonamentoDao, pubblicazioni);
        OperazioneSearch search = new OperazioneSearch(operazioneDao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneEditor editor = new OperazioneEditor(operazioneDao, pubblicazioni);
        
        addSmdComponents(insolventi,generaShow,genera,editor,search,grid);
        
        genera.setVisible(false);
        editor.setVisible(false);

        generaShow.setChangeHandler(() -> {
            insolventi.setVisible(false);
            generaShow.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            genera.edit();
        }); 
        genera.setChangeHandler(() -> {
            insolventi.setVisible(true);
            generaShow.setVisible(true);
            genera.setVisible(false);
            search.setVisible(true);
            grid.setVisible(true);
        }); 
        insolventi.setChangeHandler(()-> Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE));
        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            insolventi.setVisible(false);
            generaShow.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());   
        });
        
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            insolventi.setVisible(true);
            generaShow.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());;            
        });

        grid.addComponentColumn(op -> {
            Button button = new Button("Rigenera",VaadinIcons.ENVELOPES);
            button.setEnabled(!op.chiuso());
            button.addClickListener(click -> {
                operazioneDao.save(
                   Smd.generaOperazione(op.getPubblicazione(), 
                                        abbonamentoDao.findByAnno(op.getAnno()),
                                        op.getAnno(), 
                                        op.getMese()));
                operazioneDao.delete(op);
                grid.populate(search.find());
            });
            return button;
        });

        grid.addComponentColumn(op -> {
            Button button = new Button("Spedizioniere",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("Adp Sede",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.populate(operazioneDao.findAll());

     }

}
