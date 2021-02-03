package it.arsinfo.smd.ui.spedizione;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdAdd;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;

@SpringUI(path = SmdUI.URL_SPEDIZIONI)
@Title("Spedizioni")
@Push
public class SpedizioneUI extends SmdEditorUI<Spedizione> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private SpedizioneServiceDao dao;
                
    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = dao.findAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.findPubblicazioni();
        
        SmdAdd<Spedizione> add = new SmdAdd<Spedizione>("") {

			@Override
			public Spedizione generate() {
				return null;
			}
		};

		SpedizioneSearch search = new SpedizioneSearch(dao,anagrafica,pubblicazioni);
        SpedizioneGrid grid = new SpedizioneGrid("Spedizioni");
        SpedizioneEditor maineditor = new SpedizioneEditor(dao, anagrafica);
        SpedizioneItemEditor itemeditor = new SpedizioneItemEditor(pubblicazioni);
        SpedizioneItemGrid itemgrid = new SpedizioneItemGrid("Items");

    	SmdButton itemSave = new SmdButton("Salva", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
	    maineditor.getActions().addComponentAsFirst(itemSave.getButton());

        Button duplicaAdpNoSpese = new Button("Reinvia No Spese", VaadinIcons.HANDS_UP);
        duplicaAdpNoSpese.addClickListener(e -> duplica(maineditor.get(),InvioSpedizione.AdpSedeNoSpese));
        maineditor.getActions().addComponent(duplicaAdpNoSpese);
        
        Button duplicaAdpCorriere24h = new Button("Reinvia 24hh", VaadinIcons.HANDS_UP);
        duplicaAdpCorriere24h.addClickListener(e -> duplica(maineditor.get(),InvioSpedizione.AdpSedeCorriere24hh));
        maineditor.getActions().addComponent(duplicaAdpCorriere24h);
        
        Button duplicaAdpCorriere3gg = new Button("Reinvia 3gg", VaadinIcons.HANDS_UP);
        duplicaAdpCorriere3gg.addClickListener(e -> duplica(maineditor.get(),InvioSpedizione.AdpSedeCorriere3gg));
        maineditor.getActions().addComponent(duplicaAdpCorriere3gg);
        
        Button duplicaAdpSpesePostal = new Button("Reinvia Sp.Po.", VaadinIcons.HANDS_UP);
        duplicaAdpSpesePostal.addClickListener(e -> duplica(maineditor.get(),InvioSpedizione.AdpSede));
        maineditor.getActions().addComponent(duplicaAdpSpesePostal);
        
        BrowserWindowOpener popupOpenerA = new BrowserWindowOpener(URL_STAMPA_INDIRIZZO_SPEDIZIONE+"A");
        Button stampaA = new Button("Busta", VaadinIcons.PRINT);
        popupOpenerA.extend(stampaA);
        maineditor.getActions().addComponent(stampaA);

        BrowserWindowOpener popupOpenerB = new BrowserWindowOpener(URL_STAMPA_INDIRIZZO_SPEDIZIONE+"B");
        Button stampaB = new Button("Cartoncino", VaadinIcons.PRINT);
        popupOpenerB.extend(stampaB);
        maineditor.getActions().addComponent(stampaB);

        BrowserWindowOpener popupOpenerC = new BrowserWindowOpener(URL_STAMPA_INDIRIZZO_SPEDIZIONE+"C");
        Button stampaC = new Button("Busta Piccola", VaadinIcons.PRINT);
        popupOpenerC.extend(stampaC);
        maineditor.getActions().addComponent(stampaC);

        SmdEntityItemEditor<SpedizioneItem,Spedizione> editor = 
        		new SmdEntityItemEditor<SpedizioneItem, Spedizione>(dao,itemSave,itemgrid,itemeditor,maineditor) {
				};

        Button spedisci = new Button("Spedisci", VaadinIcons.AIRPLANE);
        spedisci.addClickListener(e -> {
        	spedisci(maineditor.get());
        	Spedizione sped = dao.findById(maineditor.get().getId());
            editor.edit(sped);
            spedisci.setEnabled(grid.getSelected().getInvioSpedizione() != InvioSpedizione.Spedizioniere);        	
        	});
        
        maineditor.getActions().addComponent(spedisci);
        spedisci.setEnabled(false);

        
        editor.addComponents(itemeditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemgrid.getComponents());

        super.init(request,add,search,editor,grid, "Spedizioni");

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader("Spedizioni"+":Edit:"+grid.getSelected().getHeader());
            if (grid.getSelected().getId() != null) {
            	popupOpenerA.setParameter("id", grid.getSelected().getId().toString());
            	popupOpenerB.setParameter("id", grid.getSelected().getId().toString());
            	popupOpenerC.setParameter("id", grid.getSelected().getId().toString());

            }
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());
            spedisci.setEnabled(grid.getSelected().getInvioSpedizione() != InvioSpedizione.Spedizioniere);
        });

        addSmdComponents(editor,search, grid);

        grid.populate(search.find());

    }
    
    private void duplica(Spedizione sped, InvioSpedizione invio) {
    	try {
    		dao.inviaDuplicato(sped, invio);
    		Notification.show("Spedizione Duplicata",Notification.Type.HUMANIZED_MESSAGE);
    	} catch (Exception e) {
    		Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
		}
	}
    
    private void spedisci(Spedizione sped) {
    	try {
    		dao.spedisci(sped);
    		Notification.show("Spedizione inviata",Notification.Type.HUMANIZED_MESSAGE);
    	} catch (Exception e) {
    		Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
		}
	}

}
