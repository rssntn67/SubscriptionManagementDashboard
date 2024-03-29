package it.arsinfo.smd.ui.storico;

import java.util.List;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.ui.vaadin.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_STORICO)
@Title("Storico Anagrafica Pubblicazioni ADP")
@Push
public class StoricoUI extends SmdEditorUI<Storico> {

	private class MyDao implements StoricoService {

		@Override
		public List<Nota> getItems(Storico t) {
			return dao.getItems(t);
		}

		@Override
		public Storico deleteItem(Storico t, Nota item) throws Exception {
			return dao.deleteItem(t, item);
		}

		@Override
		public Storico saveItem(Storico t, Nota item) throws Exception {
			return dao.saveItem(t, item);
		}

		@Override
		public List<Nota> findAllItems() {
			return dao.findAllItems();
		}

		@Override
		public Nota addItem(Storico storico) {
			Nota nota = dao.addItem(storico);
			nota.setOperatore(getLoggedInUser().getUsername());
			return nota;
		}

		@Override
		public Storico save(Storico entity) throws Exception {
        	entity.addItem(dao.getNotaOnSave(entity,getLoggedInUser().getUsername()));
        	return dao.save(entity);
		}

		@Override
		public void delete(Storico entity) throws Exception {
			dao.delete(entity);
		}

		@Override
		public Storico findById(Long id) {
			return dao.findById(id);
		}

		@Override
		public List<Storico> findAll() {
			return dao.findAll();
		}

		@Override
		public List<Storico> searchByDefault() {
			return dao.searchByDefault();
		}

		@Override
		public Storico add() {
			return dao.add();
		}

		@Override
		public List<Storico> searchBy(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione) {
			return dao.searchBy(intestatario, destinatario, pubblicazione);
		}

		@Override
		public List<Pubblicazione> findPubblicazioni() {
			return dao.findPubblicazioni();
		}

		@Override
		public List<Anagrafica> findAnagrafica() {
			return dao.findAnagrafica();
		}

		@Override
		public List<Campagna> findCampagne() {
			return dao.findCampagne();
		}

		@Override
		public Campagna getByAnno(Anno anno) {
			return dao.getByAnno(anno);
		}

		@Override
		public void aggiornaCampagna(Campagna campagna, Storico storico,String username) throws Exception {
        	storico.addItem(dao.getNotaOnSave(storico,username));
			dao.aggiornaCampagna(campagna, storico,username);
		}

		@Override
		public Nota getNotaOnSave(Storico storico, String username) {
			return dao.getNotaOnSave(storico, username);
		}

		@Override
		public List<Storico> searchBy(Anagrafica tValue) throws Exception {
			return dao.searchBy(tValue);
		}

		@Override
		public List<Abbonamento> findAbbonamento(Campagna campagna, Anagrafica intestatario, Anno anno) {
			return dao.findAbbonamento(campagna,intestatario,anno);
		}

	}
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private StoricoService dao;
    
    @Override
    protected void init(VaadinRequest request) {
    	
    	MyDao myDao = new MyDao();
        List<Anagrafica> anagrafica = myDao.findAnagrafica();
        List<Pubblicazione> pubblicazioni = myDao.findPubblicazioni();

        SmdAdd<Storico> add = new SmdAdd<>("Aggiungi Storico",myDao);
        StoricoSearch search = new StoricoSearch(myDao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("Storico");


        SmdAddItem<Nota,Storico> itemAdd = new SmdAddItem<>("Aggiungi Nota",myDao);
     	SmdButton itemDel = new SmdButton("Rimuovi Nota", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	SmdButton itemSave = new SmdButton("Salva Nota", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        SmdButtonComboBox<Campagna> update = 
                new SmdButtonComboBox<>("Seleziona",
                		myDao.findCampagne(),"Aggiorna Campagna", VaadinIcons.ARCHIVES);
            update.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
            update.getComboBox().setItemCaptionGenerator(Campagna::getCaption);
            update.getComboBox().setEmptySelectionAllowed(false);

            
        StoricoEditor maineditor = new StoricoEditor(myDao, 
                                  pubblicazioni, 
                                  anagrafica);
        maineditor.getActions().addComponents(itemDel.getComponents());
		maineditor.getActions().addComponents(itemSave.getComponents());
		maineditor.getActions().addComponents(itemAdd.getComponents());        
        maineditor.getActions().addComponents(update.getComponents());
        
        NotaGrid itemGrid = new NotaGrid("Note");
        itemGrid.getGrid().setHeight("200px");
        NotaEditor itemEditor = new NotaEditor();


        SmdEntityItemEditor<Nota,Storico> editor = new SmdEntityItemEditor<>(myDao, itemAdd, itemDel, itemSave, itemGrid, itemEditor, maineditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemGrid.getComponents());


        super.init(request, add, search, editor, grid, "Storico");
        
        addSmdComponents(editor,
                add,
                search, 
                grid);


        grid.populate(search.searchDefault());
        
        update.setChangeHandler(() -> {
            try {
            	myDao.aggiornaCampagna(update.getValue(), maineditor.get(), "admin->"+getLoggedInUser().getUsername());
                editor.onChange();
            } catch (Exception e) {
                Notification.show("Campagna ed Abbonamento non aggiornati:" + e.getMessage(), Type.ERROR_MESSAGE);
            }
        });


    }
}
