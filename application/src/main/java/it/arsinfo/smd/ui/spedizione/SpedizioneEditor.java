package it.arsinfo.smd.ui.spedizione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import it.arsinfo.smd.ui.EuroConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.SpedizioneService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

public class SpedizioneEditor
        extends SmdEntityEditor<Spedizione> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinazione");
    private final ComboBox<InvioSpedizione> invioSpedizione = 
    		new ComboBox<InvioSpedizione>("Sped.",EnumSet.allOf(InvioSpedizione.class));
    private final ComboBox<Anno> annoSped = 
    		new ComboBox<Anno>("Anno Sped",EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> meseSped = new ComboBox<Mese>("Mese Sped",EnumSet.allOf(Mese.class));
    private final TextField pesoStimato = new TextField("Peso Stimato in grammi");
    private final TextField spesePostali = new TextField("Spese Postali");

    public SpedizioneEditor(
            SpedizioneService spedizioneDao, List<Anagrafica> anagrafica) {

        super(spedizioneDao, new Binder<>(Spedizione.class) );
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
        .forField(spesePostali).withConverter(new EuroConverter("Conversione in Eur"))
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


	@Override
	public void focus(boolean persisted, Spedizione obj) {
        getSave().setVisible(false);
        getDelete().setVisible(false);
        getCancel().setVisible(false);
    }
    
}
