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
    private IncassoDao repo;
        
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoAdd add = new IncassoAdd("Aggiungi Incasso");
        IncassoUpload upload = new IncassoUpload("Incasso da Poste");
        IncassoSearch search = new IncassoSearch(repo);
        IncassoGrid grid = new IncassoGrid("");
        IncassoEditor editor = new IncassoEditor(repo);

        addSmdComponents(add,upload, editor,search, grid);
        editor.setVisible(false);

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> repo.save(incasso));
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() -> grid.populate(search.find()));
        
        editor.setChangeHandler(() -> {
            add.setVisible(true);
            upload.setVisible(true);
            search.setVisible(true);
            editor.setVisible(false);
            grid.populate(search.find());
        });

        grid.setChangeHandler(() -> {
            editor.edit(grid.getSelected());
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        grid.populate(search.findAll());

    }

}
