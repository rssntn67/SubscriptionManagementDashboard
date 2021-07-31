package it.arsinfo.smd.ui.incasso;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.ui.SmdUI;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path = SmdUI.URL_INCASSO)
@Title(SmdUI.TITLE_INCASSO)
public class IncassoGiornalieroUI extends SmdUI {

    /**
     * 
     */

    @Autowired
    private DistintaVersamentoService dao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,SmdUI.TITLE_INCASSO);
        
        IncassoGiornalieroSearch search =
        		new IncassoGiornalieroSearch(dao);
        IncassoGiornalieroGrid grid = new IncassoGiornalieroGrid("Incassi Giornalieri");
        
        
        addSmdComponents(search,grid);

        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
        });

        grid.populate(search.find());
    }
}
