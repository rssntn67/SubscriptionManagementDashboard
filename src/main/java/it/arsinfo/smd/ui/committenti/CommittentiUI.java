package it.arsinfo.smd.ui.committenti;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.versamento.OperazioneIncassoGrid;

@SpringUI(path = SmdUI.URL_VERSAMENTI_COMMITTENTI)
@Title(SmdUI.TITLE_VERSAMENTI_COMMITTENTI)
public class CommittentiUI extends SmdUI {

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
        super.init(request, SmdUI.TITLE_VERSAMENTI_COMMITTENTI);
        
        CommittentiSearch search = new CommittentiSearch(dao,abbonamentoDao.getAnagrafica());
        CommittentiGrid grid = new CommittentiGrid("Versamenti con Committenti");
        
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
