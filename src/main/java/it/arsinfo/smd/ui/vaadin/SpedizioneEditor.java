package it.arsinfo.smd.ui.vaadin;

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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.SpedizioneDao;

public class SpedizioneEditor
        extends SmdEditor<Spedizione> {

    private Button stampa = new Button("Stampa Indirizzo", VaadinIcons.PRINT);
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinazione");
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));

    private final ComboBox<StatoSpedizione> statoSpedizione = new ComboBox<StatoSpedizione>("Stato Spedizione",
            EnumSet.allOf(StatoSpedizione.class));

    private final ComboBox<Anno> annoSped = new ComboBox<Anno>("Anno Sped",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> meseSped = new ComboBox<Mese>("Mese Sped",
            EnumSet.allOf(Mese.class));

    private final TextField pesoStimato = new TextField("Peso Stimato in grammi");

    private final TextField spesePostali = new TextField("Spese Postali");

    public SpedizioneEditor(
            SpedizioneDao spedizioneDao, List<Anagrafica> anagrafica) {

        super(spedizioneDao, new Binder<>(Spedizione.class) );
        getActions().addComponent(stampa);
        stampa.addClickListener(e -> stampa());
        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinazione");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getIntestazione);
        destinatario.setReadOnly(true);
        
        invio.setEmptySelectionAllowed(false);
        invio.setReadOnly(true);

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

        invio.setEmptySelectionAllowed(false);
        invio.setReadOnly(true);

        statoSpedizione.setEmptySelectionAllowed(false);
        
        spesePostali.setReadOnly(true);
        pesoStimato.setReadOnly(true);
        HorizontalLayout dest = new HorizontalLayout();
        dest.addComponentsAndExpand(destinatario);
        setComponents(getActions(), 
        			dest,
                      new HorizontalLayout(invio,invioSpedizione,statoSpedizione),
                      new HorizontalLayout(meseSped,annoSped),
                      new HorizontalLayout(pesoStimato,spesePostali)
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
        
        getBinder().forField(invio)
        .asRequired().bind(Spedizione::getInvio,Spedizione::setInvio);

        getBinder().forField(invioSpedizione)
        .asRequired().bind(Spedizione::getInvioSpedizione,Spedizione::setInvioSpedizione);

        getBinder().forField(statoSpedizione)
        .asRequired().bind(Spedizione::getStatoSpedizione,Spedizione::setStatoSpedizione);

        getBinder().forField(meseSped)
        .asRequired().bind(Spedizione::getMeseSpedizione,Spedizione::setMeseSpedizione);

        getBinder().forField(annoSped)
        .asRequired().bind(Spedizione::getAnnoSpedizione,Spedizione::setAnnoSpedizione);

    }

    @Override
    public void focus(boolean persisted, Spedizione obj) {
        getSave().setEnabled(obj.getStatoSpedizione() != StatoSpedizione.INVIATA); 
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
    	subContent.addComponent(new Label(indirizzo.getCap() + " " + indirizzo.getCitta()));
    	subContent.addComponent(new Label(indirizzo.getProvincia().name()));
    	subContent.addComponent(new Label(indirizzo.getPaese().getNome()));

    	subWindow.setContent(subContent);
    	subWindow.center();
    	UI.getCurrent().addWindow(subWindow);
    }

}
