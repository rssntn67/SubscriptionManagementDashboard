package it.arsinfo.smd.ui.distinta;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.vaadin.SmdAdd;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;

@SpringUI(path = SmdUI.URL_DISTINTA_VERSAMENTI)
@Title(SmdUI.TITLE_DISTINTA_VERSAMENTI)
public class DistintaVersamentoUI extends SmdEditorUI<DistintaVersamento> {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private DistintaVersamentoService dao;
    
    @Override
    protected void init(VaadinRequest request) {
        SmdAdd<DistintaVersamento> add = new SmdAdd<>("Aggiungi Distinta",dao);
        DistintaVersamentoSearch search = new DistintaVersamentoSearch(dao);
        DistintaVersamentoGrid grid = new DistintaVersamentoGrid("Distinte Versamenti");

        SmdAddItem<Versamento,DistintaVersamento> itemAdd = new SmdAddItem<>("Aggiungi Versamento",dao);
     	SmdButton itemDel = new SmdButton("Rimuovi Versamento", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	SmdButton itemSave = new SmdButton("Salva Versamento", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        DistintaVersamentoEditor maineditor = new DistintaVersamentoEditor(dao);
	    maineditor.getActions().addComponents(itemDel.getComponents());
		maineditor.getActions().addComponents(itemSave.getComponents());
		maineditor.getActions().addComponents(itemAdd.getComponents());
        VersamentoGrid itemGrid = new VersamentoGrid("Versamenti");
        VersamentoEditor itemEditor = new VersamentoEditor();

        SmdEntityItemEditor<Versamento,DistintaVersamento> editor = new SmdEntityItemEditor<>(dao, itemAdd, itemDel, itemSave,itemGrid, itemEditor, maineditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemGrid.getComponents());

        super.init(request, add, search, editor, grid, SmdUI.TITLE_DISTINTA_VERSAMENTI);
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);
        
        itemGrid.setChangeHandler(() -> {
        	
        });

        grid.populate(search.searchDefault());

    }  
    
}
