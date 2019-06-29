package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.EstrattoContoDao;

public class EstrattoContoEditor
        extends SmdEditor<EstrattoConto> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazione");
    private final ComboBox<TipoEstrattoConto> tipoEstrattoconto = new ComboBox<TipoEstrattoConto>("Omaggio",
                                                                    EnumSet.allOf(TipoEstrattoConto.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));
    
    private final ComboBox<Anno> annoInizio = new ComboBox<Anno>("Anno Inizio",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Anno> annoFine = new ComboBox<Anno>("Anno Fine",
            EnumSet.allOf(Anno.class));

    private final ComboBox<Mese> meseInizio = new ComboBox<Mese>("Mese Inizio",
            EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> meseFine = new ComboBox<Mese>("Mese Fine",
            EnumSet.allOf(Mese.class));

    private final TextField numero = new TextField("Quant.");

    private final TextField importo = new TextField("Importo");

    private final TextField spesePostali = new TextField("Spese Postali");

    public EstrattoContoEditor(
            EstrattoContoDao anagraficaPubblicazioneDao,
            List<Pubblicazione> pubblicazioni, List<Anagrafica> anagrafica) {

        super(anagraficaPubblicazioneDao, new Binder<>(EstrattoConto.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        invio.setEmptySelectionAllowed(false);
        tipoEstrattoconto.setEmptySelectionAllowed(false);

        annoInizio.setEmptySelectionAllowed(false);
        annoInizio.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoInizio.setSelectedItem(Smd.getAnnoCorrente());
        annoFine.setEmptySelectionAllowed(false);
        annoFine.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoFine.setSelectedItem(Smd.getAnnoCorrente());
        meseInizio.setEmptySelectionAllowed(false);
        meseInizio.setItemCaptionGenerator(Mese::getNomeBreve);
        meseInizio.setSelectedItem(Mese.GENNAIO);
        meseFine.setEmptySelectionAllowed(false);
        meseFine.setItemCaptionGenerator(Mese::getNomeBreve);
        meseFine.setSelectedItem(Mese.DICEMBRE);
        invioSpedizione.setEmptySelectionAllowed(false);
        invioSpedizione.setSelectedItem(InvioSpedizione.Spedizioniere);
        
        setComponents(getActions(), 
                      new HorizontalLayout(pubblicazione,numero,meseInizio,annoInizio,meseFine,annoFine),
                      new HorizontalLayout(destinatario,tipoEstrattoconto, invio,invioSpedizione),
                      new HorizontalLayout(importo,spesePostali)
                      );
 
        getBinder()
        .forField(importo).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(EstrattoConto::getImporto,EstrattoConto::setImporto);
 
        getBinder()
        .forField(spesePostali).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(EstrattoConto::getSpesePostali,EstrattoConto::setSpesePostali);
 
        getBinder()
            .forField(numero)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(EstrattoConto::getNumero, EstrattoConto::setNumero);
        
        getBinder()
        .forField(destinatario)
        .asRequired()
        .withValidator(p -> p != null, "Destinatario deve essere selezionato")
        .bind(EstrattoConto::getDestinatario,EstrattoConto::setDestinatario);

 
        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(EstrattoConto::getPubblicazione,EstrattoConto::setPubblicazione);
        
        getBinder().forField(invio)
        .asRequired().bind(EstrattoConto::getInvio,EstrattoConto::setInvio);

        getBinder().forField(tipoEstrattoconto)
        .asRequired().bind(EstrattoConto::getTipoEstrattoConto,EstrattoConto::setTipoEstrattoConto);


    }

    @Override
    public void focus(boolean persisted, EstrattoConto obj) {
        destinatario.setReadOnly(persisted);
        pubblicazione.setReadOnly(persisted);
        numero.setReadOnly(persisted);
        tipoEstrattoconto.setReadOnly(persisted);
        invio.setReadOnly(persisted);
        importo.setVisible(persisted);
        spesePostali.setVisible(persisted);

        invioSpedizione.setVisible(!persisted);
        annoInizio.setVisible(!persisted);
        annoFine.setVisible(!persisted);
        meseInizio.setVisible(!persisted);
        meseFine.setVisible(!persisted);
        getDelete().setEnabled(!persisted);
        destinatario.focus();        
    }

    public Anno getAnnoInizio() {
        return annoInizio.getValue();
    }
    
    public Anno getAnnoFine() {
        return annoInizio.getValue();
    }
    
    public Mese getMeseInizio() {
        return meseInizio.getValue();
    }
    
    public Mese getMeseFine() {
        return meseFine.getValue();
    }

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione.getValue();
    }
    
    public void setDestinatario(Anagrafica anagrafica) {
        destinatario.setSelectedItem(anagrafica);
    }

}
