package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;

@SpringUI(path = SmdUI.URL_VERSAMENTI)
@Title("Versamenti")
public class VersamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    VersamentoDao versamentoDao;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
        VersamentoSearch search = new VersamentoSearch(versamentoDao);
        VersamentoGrid grid = new VersamentoGrid("");
        VersamentoEditor editor = new VersamentoEditor(versamentoDao) {
            @Override
            public void focus(boolean persisted, Versamento versamento) {
                super.focus(persisted, versamento);
                getDelete().setEnabled(false);
                getSave().setEnabled(false);
                getCancel().setEnabled(false);
            }
        };
        
        addSmdComponents(search,editor,grid);
        
        editor.setVisible(false);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                editor.setVisible(false);
                search.setVisible(true);
            } else {
                editor.edit(grid.getSelected());
                search.setVisible(false);
            }
        });
        
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            grid.populate(search.find());
        });

    }

}
