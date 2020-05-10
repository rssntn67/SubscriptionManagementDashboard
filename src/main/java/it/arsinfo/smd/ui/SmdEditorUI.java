package it.arsinfo.smd.ui;

import com.vaadin.server.VaadinRequest;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.ui.vaadin.SmdAdd;
import it.arsinfo.smd.ui.vaadin.SmdEditor;
import it.arsinfo.smd.ui.vaadin.SmdGrid;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public abstract class SmdEditorUI<T extends SmdEntity> extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;
    
    protected void init(VaadinRequest request,SmdAdd<T> add ,SmdSearch<T> search,SmdEditor<T> editor,SmdGrid<T> grid,String header) {
        super.init(request, header);
                
        add.setChangeHandler(() -> {
            setHeader(header+":Nuovo");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(header+":Edit:"+grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            editor.setVisible(false);
            setHeader(header);
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
        });

    }    
}
