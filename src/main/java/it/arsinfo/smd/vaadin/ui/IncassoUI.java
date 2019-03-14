package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends SmdUI {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    IncassoDao repo;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoUpload upload = new IncassoUpload();
        IncassoSearch search = new IncassoSearch(repo);
        IncassoGrid grid = new IncassoGrid("");
        IncassoEditor editor = new IncassoEditor(repo);

        addSmdComponents(upload, editor,search, grid);
        editor.setVisible(false);
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> repo.save(incasso));
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() -> grid.populate(search.find()));
        
        
        grid.populate(search.findAll());

    }

}
