package it.arsinfo.smd.ui.ddt;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.DocumentoTrasportoDao;
import it.arsinfo.smd.dao.DocumentiTrasportoCumulatiDao;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_DDT)
@Title(SmdUI.TITLE_DDT)
public class DocumentiTrasportoCumulatiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private DocumentiTrasportoCumulatiDao dao;
    @Autowired
    private DocumentoTrasportoDao itemDao; 

    @Override
    protected void init(VaadinRequest request) {
    	super.init(request, "Ddt");
        DocumentiTrasportoCumulatiGrid grid = new DocumentiTrasportoCumulatiGrid("DDT per anno");
        DocumentiTrasportoGrid ddtGrid = new DocumentiTrasportoGrid("DDT");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        addSmdComponents(indietro,grid,ddtGrid);
        
        indietro.setVisible(false);
    	ddtGrid.setVisible(false);
        
        indietro.setChangeHandler(() -> {
        	showMenu();
        	grid.populate(dao.findAll());
        	ddtGrid.setVisible(false);
        	indietro.setVisible(false);
        });
        
        grid.setChangeHandler(() -> {
        	if (grid.getSelected() == null) {
            	showMenu();
            	ddtGrid.setVisible(false);
            	indietro.setVisible(false);
        	} else {
        		hideMenu();
        		ddtGrid.populate(itemDao.findByDocumentiTrasportoCumulati(grid.getSelected()));
        		indietro.setVisible(true);
        	}
        });
        
        ddtGrid.setChangeHandler(()->{});
        
        grid.populate(dao.findAll());

    }
    
}
