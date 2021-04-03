package it.arsinfo.smd.ui.abbonamento;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Button;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdEditorUI<Abbonamento> {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    AbbonamentoServiceDao dao;
    
    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = dao.getAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.getPubblicazioni();
        List<Campagna> campagne = dao.getCampagne();
        AbbonamentoAdd add = new AbbonamentoAdd("Aggiungi abbonamento");
        if (anagrafica.size() == 0) {
            add.setVisible(false);
        }
        AbbonamentoSearch search = new AbbonamentoSearch(dao,campagne,pubblicazioni,anagrafica);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        
        RivistaAbbonamentoAdd itemAdd = new RivistaAbbonamentoAdd("Aggiungi Rivista");
     	
        SmdButton itemDel = new SmdButton("Rimuovi Rivista", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	
	    SmdButton itemSave = new SmdButton("Salva Rivista", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
	    
	    AbbonamentoEditor maineditor = new AbbonamentoEditor(dao,anagrafica,campagne);
	    maineditor.getActions().addComponents(itemDel.getButton());
		maineditor.getActions().addComponents(itemSave.getButton());
		maineditor.getActions().addComponents(itemAdd.getButton());
        
		RivistaAbbonamentoGrid itemGrid = new RivistaAbbonamentoGrid("Riviste in Abbonamento");
        
		RivistaAbbonamentoEditor itemEditor = new RivistaAbbonamentoEditor(pubblicazioni, anagrafica);
   	    
        AbbonamentoRivisteEditor editor = new AbbonamentoRivisteEditor(dao, itemAdd, itemDel, itemSave,itemGrid, itemEditor, maineditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemGrid.getComponents());

        super.init(request, add, search, editor, grid, "Abbonamento");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        grid.populate(search.findAll());

    }

}
