package it.arsinfo.smd.ui.spedizione;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Abbonamento;
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
    AnagraficaDao anagraficaDao;

    @Autowired
    SpedizioneServiceDao dao;
    
    @Autowired
    SpedizioneItemDao spedizioneItemDao;
    
    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    PubblicazioneDao pubblicazioneDao; 
    
    @Autowired
    SmdService smdService;
    

    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Abbonamento> abbonamenti = abbonamentoDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        
        SmdAdd<Spedizione> add = new SmdAdd<Spedizione>("") {

			@Override
			public Spedizione generate() {
				return null;
			}
		};

		SpedizioneSearch search = new SpedizioneSearch(dao,abbonamenti,anagrafica,pubblicazioni);
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
        
        Button stampa = new Button("Stampa", VaadinIcons.PRINT);
        stampa.addClickListener(e -> stampa(maineditor.get()));
        maineditor.getActions().addComponent(stampa);
        
        SmdEntityItemEditor<SpedizioneItem,Spedizione> editor = 
        		new SmdEntityItemEditor<SpedizioneItem, Spedizione>(dao,itemSave,itemgrid,itemeditor,maineditor) {
				};
        editor.addComponents(itemeditor.getComponents());
        editor.addComponents(maineditor.getComponents());
        editor.addComponents(itemgrid.getComponents());

        super.init(request,add,search,editor,grid, "Spedizioni");

        addSmdComponents(editor,search, grid);

        grid.populate(search.find());

    }
    
    private void duplica(Spedizione sped, InvioSpedizione invio) {
    	try {
    		smdService.inviaDuplicato(sped, invio);
    		Notification.show("Spedizione Duplicata",Notification.Type.HUMANIZED_MESSAGE);
    	} catch (Exception e) {
    		Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
		}
	}
    
    private void stampa(Spedizione sped) {
    	Indirizzo indirizzo = smdService.genera(sped);
    	Window subWindow = new Window();
    	VerticalLayout subContent = new VerticalLayout();
    	subContent.addComponent(new Label(indirizzo.getIntestazione()));
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		subContent.addComponent(new Label(indirizzo.getSottoIntestazione()));
    	}
    	subContent.addComponent(new Label(indirizzo.getIndirizzo()));
    	subContent.addComponent(
			new Label(
				indirizzo.getCap() + " " + indirizzo.getCitta() + " ("+indirizzo.getProvincia().name()+")"
			)
		);
    	subContent.addComponent(new Label(indirizzo.getPaese().getNome()));

    	subWindow.setContent(subContent);
    	subWindow.center();
    	UI.getCurrent().addWindow(subWindow);
    }


}
