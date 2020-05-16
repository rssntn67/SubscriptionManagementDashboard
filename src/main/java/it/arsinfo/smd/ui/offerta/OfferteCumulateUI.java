package it.arsinfo.smd.ui.offerta;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.repository.OffertaDao;
import it.arsinfo.smd.dao.repository.OfferteCumulateDao;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_OFFERTE)
@Title(SmdUI.TITLE_OFFERTE)
public class OfferteCumulateUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private OfferteCumulateDao dao;
    private OffertaDao itemDao; 

    @Override
    protected void init(VaadinRequest request) {
    	super.init(request, "Offerte");
        OfferteCumulateGrid grid = new OfferteCumulateGrid("Anagrafiche");
        OfferteGrid offerteGrid = new OfferteGrid("Offerte");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        addSmdComponents(indietro,grid,offerteGrid);
        
        indietro.setVisible(false);
    	offerteGrid.setVisible(false);
        
        indietro.setChangeHandler(() -> {
        	showMenu();
        	grid.populate(dao.findAll());
        	offerteGrid.setVisible(false);
        	indietro.setVisible(false);
        });
        
        grid.setChangeHandler(() -> {
        	if (grid.getSelected() == null) {
            	showMenu();
            	offerteGrid.setVisible(false);
            	indietro.setVisible(false);
        	} else {
        		hideMenu();
        		offerteGrid.populate(itemDao.findByOfferteCumulate(grid.getSelected()));
        		indietro.setVisible(true);
        	}
        });
        
        offerteGrid.setChangeHandler(()->{});
        
        grid.populate(dao.findAll());

    }
    
}
