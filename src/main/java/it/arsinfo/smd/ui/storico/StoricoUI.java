package it.arsinfo.smd.ui.storico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.dao.StoricoServiceDaoImpl;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonComboBox;

@SpringUI(path = SmdUI.URL_STORICO)
@Title("Storico Anagrafica Pubblicazioni ADP")
@Push
public class StoricoUI extends SmdEditorUI<Storico> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private StoricoServiceDaoImpl dao;

    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = dao.findAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.findPubblicazioni();

        StoricoAdd add = new StoricoAdd("Aggiungi Storico");
        StoricoSearch search = new StoricoSearch(dao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("Storico");


        NotaAdd itemAdd = new NotaAdd("Aggiungi Nota");
     	SmdButton itemDel = new SmdButton("Rimuovi Nota", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	SmdButton itemSave = new SmdButton("Salva Nota", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        SmdButtonComboBox<Campagna> update = 
                new SmdButtonComboBox<Campagna>("Seleziona", 
                		dao.findCampagne(),"Aggiorna Campagna", VaadinIcons.ARCHIVES);
            update.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
            update.getComboBox().setItemCaptionGenerator(Campagna::getCaption);
            update.getComboBox().setEmptySelectionAllowed(false);

            
        StoricoEditor maineditor = new StoricoEditor(dao, 
                                  pubblicazioni, 
                                  anagrafica);
        maineditor.getActions().addComponents(itemDel.getComponents());
		maineditor.getActions().addComponents(itemSave.getComponents());
		maineditor.getActions().addComponents(itemAdd.getComponents());        
        maineditor.getActions().addComponents(update.getComponents());
        maineditor.getSave().addClickListener(e -> {
        	maineditor.get().addItem(getNotaOnSave(maineditor.get()));
        	maineditor.save();
        });
        
        update.setChangeHandler(() -> {
            try {
            	maineditor.get().addItem(getNotaOnSave(maineditor.get()));
            	dao.aggiornaCampagna(update.getValue(), maineditor.get(), "admin->"+getLoggedInUser().getUsername());
                maineditor.onChange();
            } catch (Exception e) {
                Notification.show("Campagna ed Abbonamento non aggiornati:" + e.getMessage(), Type.ERROR_MESSAGE);
                return;                    
            }
        });

        NotaGrid itemGrid = new NotaGrid("Note");
        itemGrid.getGrid().setHeight("200px");
        NotaEditor itemEditor = new NotaEditor();


        StoricoNotaEditor editor = new StoricoNotaEditor(dao, itemAdd, itemDel, itemSave, itemGrid, itemEditor, maineditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemGrid.getComponents());


        super.init(request, add, search, editor, grid, "Storico");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);


        grid.populate(search.findAll());

    }
    
    private  Nota getNotaOnSave(Storico storico) {
        Nota nota = new Nota(storico);
        nota.setOperatore("admin->"+getLoggedInUser().getUsername());
        if (storico.getId() == null) {
            nota.setDescription("Nuovo: " + storico.toString());
        } else {
            nota.setDescription("Aggiornato: " + storico.toString());                    
        }        
        return nota;
    }
}
