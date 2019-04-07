package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_PROSPETTI)
@Title("Statistiche ADP")
public class ProspettoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    ProspettoDao prospettoDao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Statistiche");
        
        ProspettoGrid grid = new ProspettoGrid("Statistiche");
        
        addSmdComponents(grid);
                
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            Notification.show(grid.getSelected().toString());   
        });
        
        grid.populate(prospettoDao.findAll());

     }

}
