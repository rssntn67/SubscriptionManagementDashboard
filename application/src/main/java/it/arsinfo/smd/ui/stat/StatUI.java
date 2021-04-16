package it.arsinfo.smd.ui.stat;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.ui.service.api.AbbonamentoConRivisteService;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_STAT)
@Title(SmdUI.TITLE_STAT)
public class StatUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 6407425404499250763L;

    @Autowired
    private AbbonamentoConRivisteService dao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,SmdUI.TITLE_STAT);
        
        StatSearch search = 
        		new StatSearch(dao);
        AbbonamentoConRivisteGrid grid = new AbbonamentoConRivisteGrid("abbonamenti");
        
        
        addSmdComponents(search,grid);
        

        grid.getGrid().setHeight("600px");

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
        });
        
                
        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });

        grid.populate(search.findNone());
    }
}
