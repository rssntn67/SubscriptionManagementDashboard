package it.arsinfo.smd.ui.campagna;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.CampagnaServiceDao;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_CAMPAGNA)
@Title("Campagna Abbonamenti ADP")
public class CampagnaUI extends SmdEditorUI<Campagna> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private CampagnaServiceDao campagnaDao;

    @Override
    protected void init(VaadinRequest request) {
        CampagnaAdd add = new CampagnaAdd("Genera una nuova Campagna");
        CampagnaSearch search = new CampagnaSearch(campagnaDao);
        CampagnaGrid grid = new CampagnaGrid("Campagne");
        CampagnaEditor editor = new CampagnaEditor(campagnaDao);
        init(request, add, search, editor, grid, "Campagne Abbonamenti");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        editor.setVisible(false);
        
        grid.populate(search.findAll());

    }

}
