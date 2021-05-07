package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.themes.ValoTheme;
import it.arsinfo.smd.bollettino.api.BollettinoService;
import it.arsinfo.smd.config.CcpConfig;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdAdd;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringUI(path = SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdEditorUI<Abbonamento> {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    private AbbonamentoService dao;

    @Autowired
    private BollettinoService bollettinoService;

    @Autowired
    private CcpConfig ccpConfig;
    
    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = dao.getAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.getPubblicazioni();
        List<Campagna> campagne = dao.getCampagne();
        SmdAdd<Abbonamento> add = new SmdAdd<>("Aggiungi abbonamento",dao);
        if (anagrafica.size() == 0) {
            add.setVisible(false);
        }
        AbbonamentoSearch search = new AbbonamentoSearch(dao,campagne,pubblicazioni,anagrafica);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        
        SmdAddItem<RivistaAbbonamento,Abbonamento> itemAdd = new SmdAddItem<>("Aggiungi Rivista",dao);
     	
        SmdButton itemDel = new SmdButton("Rimuovi Rivista", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	
	    SmdButton itemSave = new SmdButton("Salva Rivista", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
	    
	    AbbonamentoEditor maineditor =
                new AbbonamentoEditor(
                        dao,
                        bollettinoService,
                        ccpConfig,
                        anagrafica,
                        campagne);
	    maineditor.getActions().addComponents(itemDel.getButton());
		maineditor.getActions().addComponents(itemSave.getButton());
		maineditor.getActions().addComponents(itemAdd.getButton());
        
		RivistaAbbonamentoGrid itemGrid = new RivistaAbbonamentoGrid("Riviste in Abbonamento");
        
		RivistaAbbonamentoEditor itemEditor = new RivistaAbbonamentoEditor(pubblicazioni, anagrafica);
   	    
        SmdEntityItemEditor<RivistaAbbonamento,Abbonamento> editor = new SmdEntityItemEditor<>(dao, itemAdd, itemDel, itemSave,itemGrid, itemEditor, maineditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemGrid.getComponents());

        super.init(request, add, search, editor, grid, "Abbonamento");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        grid.populate(search.searchDefault());

    }

}
