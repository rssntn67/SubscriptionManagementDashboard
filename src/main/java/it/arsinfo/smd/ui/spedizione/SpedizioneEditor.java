package it.arsinfo.smd.ui.spedizione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.vaadin.SmdRepositoryDaoEditor;

public class SpedizioneEditor
        extends SmdRepositoryDaoEditor<Spedizione> {

    private Button stampa = new Button("Stampa Indirizzo", VaadinIcons.PRINT);
    private Button duplicaAdpNoSpese = new Button("Reinvia No Spese", VaadinIcons.HANDS_UP);
    private Button duplicaAdpCorriere24h = new Button("Reinvia 24hh", VaadinIcons.HANDS_UP);
    private Button duplicaAdpCorriere3gg = new Button("Reinvia 3gg", VaadinIcons.HANDS_UP);
    private Button duplicaAdpSpesePostal = new Button("Reinvia Sp.Po.", VaadinIcons.HANDS_UP);
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinazione");
    private final ComboBox<InvioSpedizione> invioSpedizione = 
    		new ComboBox<InvioSpedizione>("Sped.",EnumSet.allOf(InvioSpedizione.class));
    private final ComboBox<Anno> annoSped = 
    		new ComboBox<Anno>("Anno Sped",EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> meseSped = new ComboBox<Mese>("Mese Sped",EnumSet.allOf(Mese.class));
    private final TextField pesoStimato = new TextField("Peso Stimato in grammi");
    private final TextField spesePostali = new TextField("Spese Postali");

    public SpedizioneEditor(
            SpedizioneDao spedizioneDao, List<Anagrafica> anagrafica) {

        super(spedizioneDao, new Binder<>(Spedizione.class) );
        getActions().addComponents(stampa,duplicaAdpNoSpese,duplicaAdpCorriere24h,duplicaAdpCorriere3gg,duplicaAdpSpesePostal);
        stampa.addClickListener(e -> stampa());
        duplicaAdpNoSpese.addClickListener(e -> duplica(InvioSpedizione.AdpSedeNoSpese));
        duplicaAdpCorriere24h.addClickListener(e -> duplica(InvioSpedizione.AdpSedeCorriere24hh));
        duplicaAdpCorriere3gg.addClickListener(e -> duplica(InvioSpedizione.AdpSedeCorriere3gg));
        duplicaAdpSpesePostal.addClickListener(e -> duplica(InvioSpedizione.AdpSede));
        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinazione");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);
        destinatario.setReadOnly(true);
        
        annoSped.setEmptySelectionAllowed(false);
        annoSped.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoSped.setSelectedItem(Anno.getAnnoCorrente());
        annoSped.setReadOnly(true);

        meseSped.setEmptySelectionAllowed(false);
        meseSped.setItemCaptionGenerator(Mese::getNomeBreve);
        meseSped.setSelectedItem(Mese.GENNAIO);
        meseSped.setReadOnly(true);

        invioSpedizione.setEmptySelectionAllowed(false);
        invioSpedizione.setReadOnly(true);
        
        spesePostali.setReadOnly(true);
        pesoStimato.setReadOnly(true);
        HorizontalLayout dest = new HorizontalLayout();
        dest.addComponentsAndExpand(destinatario);
        setComponents(getActions(), 
        			dest,
                  new HorizontalLayout(invioSpedizione,meseSped,annoSped,pesoStimato,spesePostali)
                      );
 
        getBinder()
        .forField(spesePostali).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(Spedizione::getSpesePostali,Spedizione::setSpesePostali);
  
        getBinder()
            .forField(pesoStimato)
            .withConverter(new StringToIntegerConverter("Inserire un Numero"))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(Spedizione::getPesoStimato, Spedizione::setPesoStimato);
        
        getBinder()
        .forField(destinatario)
        .asRequired()
        .withValidator(p -> p != null, "Destinatario deve essere selezionato")
        .bind(Spedizione::getDestinatario,Spedizione::setDestinatario);
        
        getBinder().forField(invioSpedizione)
        .asRequired().bind(Spedizione::getInvioSpedizione,Spedizione::setInvioSpedizione);

        getBinder().forField(meseSped)
        .asRequired().bind(Spedizione::getMeseSpedizione,Spedizione::setMeseSpedizione);

        getBinder().forField(annoSped)
        .asRequired().bind(Spedizione::getAnnoSpedizione,Spedizione::setAnnoSpedizione);

    }

    private void duplica(InvioSpedizione invio) {
    	try {
    		getSmdService().inviaDuplicato(get(), invio);
    		Notification.show("Spedizione Duplicata",Notification.Type.HUMANIZED_MESSAGE);
    	} catch (Exception e) {
    		Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
    public void focus(boolean persisted, Spedizione obj) {
        getSave().setEnabled(false); 
        getDelete().setEnabled(false);        
    }
    
    public void stampa() {
    	Indirizzo indirizzo = getSmdService().genera(get());
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
