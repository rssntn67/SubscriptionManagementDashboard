package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

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

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Operazioni");
        
        SmdButton insolventi = new SmdButton("Insolventi", VaadinIcons.ENVELOPES);
        OperazioneSearch search = new OperazioneSearch(operazioneDao, pubblicazioneDao.findAll());
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        
        addSmdComponents(insolventi,search,grid);
        
        insolventi.setChangeHandler(()-> Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE));

        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            Notification.show(grid.getSelected().toString());   
        });

        grid.addComponentColumn(prospetto -> {
            Button button = new Button("Spedizioniere",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.addComponentColumn(prospetto -> {
            Button button = new Button("Adp Sede",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.populate(operazioneDao.findAll());

     }

}
