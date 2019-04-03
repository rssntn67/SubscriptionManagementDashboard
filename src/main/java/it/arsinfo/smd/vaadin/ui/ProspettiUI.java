package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.vaadin.model.SmdButton;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_PROSPETTI)
@Title("Prospetti ADP")
public class ProspettiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    ProspettoDao prospettoDao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Prospetti");
        
        SmdButton insolventi = new SmdButton("Insolventi", VaadinIcons.ENVELOPES);
        ProspettoGrid grid = new ProspettoGrid("Statistiche");
        
        addSmdComponents(insolventi,grid);
        
        insolventi.setChangeHandler(()-> Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE));
        
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            Notification.show(grid.getSelected().toString());   
        });

        grid.addComponentColumn(prospetto -> {
            Button button = new Button("Spedizioni",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.addComponentColumn(prospetto -> {
            Button button = new Button("Spedizioni Sede",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Non ancora supportato", Notification.Type.WARNING_MESSAGE);
            });
            return button;
        });
        
        grid.populate(prospettoDao.findAll());

     }

}
