package it.arsinfo.smd.ui.anagrafica;

import it.arsinfo.smd.ui.vaadin.SmdAdd;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_ANAGRAFICA)
@Title("Anagrafica ADP")
public class AnagraficaUI extends SmdEditorUI<Anagrafica> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaService anagraficaServiceDao;

    @Override
    protected void init(VaadinRequest request) {
        SmdAdd<Anagrafica> add = new SmdAdd<>("Aggiungi ad Anagrafica",anagraficaServiceDao);
        AnagraficaSearch search = new AnagraficaSearch(anagraficaServiceDao);
        AnagraficaGrid grid = new AnagraficaGrid("Anagrafiche");
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaServiceDao);
        super.init(request,add,search,editor,grid, "Anagrafica");        
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        editor.setVisible(false);
        
        grid.populate(search.searchDefault());

    }
    
}
