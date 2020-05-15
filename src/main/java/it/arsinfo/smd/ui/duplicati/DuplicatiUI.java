package it.arsinfo.smd.ui.duplicati;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.versamento.OperazioneIncassoGrid;

@SpringUI(path = SmdUI.URL_VERSAMENTI_DUPLICATI)
@Title("Versamenti Duplicati")
public class DuplicatiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private VersamentoServiceDao dao;

    @Autowired
    private AbbonamentoServiceDao abbonamentoDao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");
        
        DuplicatiSearch search = new DuplicatiSearch(dao,abbonamentoDao.getAnagrafica());
        DuplicatiGrid grid = new DuplicatiGrid("Versamenti Duplicati");
        
        OperazioneIncassoGrid abbonamentiAssociatiGrid = new OperazioneIncassoGrid("Operazioni Incasso Associate");

        addSmdComponents(search,grid,abbonamentiAssociatiGrid);
        
        abbonamentiAssociatiGrid.setVisible(false);

        grid.getGrid().setHeight("300px");
        abbonamentiAssociatiGrid.getGrid().setHeight("300px");

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() != null) {
                abbonamentiAssociatiGrid.populate(dao.getAssociati(grid.getSelected()));
            } else {
                abbonamentiAssociatiGrid.setVisible(false);
            }
        });
                
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
        });
                
        grid.populate(search.find());
    }
}
