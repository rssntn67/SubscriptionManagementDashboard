package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;
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
    private IncassoDao incassoDao;
    @Autowired    
    private VersamentoDao versamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoAdd add = new IncassoAdd("Aggiungi Incasso");
        IncassoUpload upload = new IncassoUpload("Incasso da Poste");
        IncassoSearch search = new IncassoSearch(incassoDao);
        IncassoGrid grid = new IncassoGrid("");
        IncassoEditor editor = new IncassoEditor(incassoDao);
        VersamentoGrid versgrid = new VersamentoGrid("Versamenti");
        
        addSmdComponents(add,upload, editor,versgrid,search, grid);
        editor.setVisible(false);
        versgrid.setVisible(false);

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> incassoDao.save(incasso));
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() -> grid.populate(search.find()));
        
        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            add.setVisible(true);
            upload.setVisible(true);
            search.setVisible(true);
            editor.setVisible(false);
        });

        grid.setChangeHandler(() -> {
            editor.edit(grid.getSelected());
            versgrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        grid.populate(search.findAll());

    }

}
