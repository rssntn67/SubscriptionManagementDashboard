package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.SpedizioneDao;

public class SpedizioneEditor
        extends SmdEditor<Spedizione> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
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

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);
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
        setComponents(getActions(), 
                      new HorizontalLayout(destinatario,invio,invioSpedizione,statoSpedizione),
                      new HorizontalLayout(meseSped,annoSped),
                      new HorizontalLayout(pesoStimato,spesePostali)
                      );
 
        getBinder()
        .forField(spesePostali).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(Spedizione::getSpesePostali,Spedizione::setSpesePostali);
  
        getBinder()
            .forField(pesoStimato)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
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

}
