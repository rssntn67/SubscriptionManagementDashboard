package it.arsinfo.smd.ui.abbonamento;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoRivista;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.vaadin.SmdItemEditor;

public class RivistaAbbonamentoEditor
        extends SmdItemEditor<RivistaAbbonamento> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazione");
    
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<TipoAbbonamentoRivista> tipoAbbonamentoRivista = new ComboBox<TipoAbbonamentoRivista>("Tipo",
                                                                    EnumSet.allOf(TipoAbbonamentoRivista.class));
    
    private final ComboBox<Anno> annoInizio = new ComboBox<Anno>("Anno Inizio",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Anno> annoFine = new ComboBox<Anno>("Anno Fine",
            EnumSet.allOf(Anno.class));

    private final ComboBox<Mese> meseInizio = new ComboBox<Mese>("Mese Inizio",
            EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> meseFine = new ComboBox<Mese>("Mese Fine",
            EnumSet.allOf(Mese.class));
    private final ComboBox<StatoRivista> statoRivista = new ComboBox<StatoRivista>("Stato",
            EnumSet.allOf(StatoRivista.class));

    private final TextField numeroTotaleRiviste = new TextField("Numero Totale Riviste");
    private final TextField numero = new TextField("Quant.");

    private final TextField importo = new TextField("Importo");
    
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));

    public RivistaAbbonamentoEditor(
            List<Pubblicazione> pubblicazioni, List<Anagrafica> anagrafica) {

        super(new Binder<>(RivistaAbbonamento.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        
        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);
        
        invioSpedizione.setEmptySelectionAllowed(false);

        tipoAbbonamentoRivista.setEmptySelectionAllowed(false);

        annoInizio.setEmptySelectionAllowed(false);
        annoInizio.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoInizio.setSelectedItem(Anno.getAnnoCorrente());
        annoFine.setEmptySelectionAllowed(false);
        annoFine.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoFine.setSelectedItem(Anno.getAnnoCorrente());
        meseInizio.setEmptySelectionAllowed(false);
        meseInizio.setItemCaptionGenerator(Mese::getNomeBreve);
        meseInizio.setSelectedItem(Mese.GENNAIO);
        meseFine.setEmptySelectionAllowed(false);
        meseFine.setItemCaptionGenerator(Mese::getNomeBreve);
        meseFine.setSelectedItem(Mese.DICEMBRE);
        
        HorizontalLayout lay = new HorizontalLayout(pubblicazione,tipoAbbonamentoRivista,invioSpedizione);
        lay.addComponentsAndExpand(destinatario);
        setComponents(
    					lay,
                      new HorizontalLayout(meseInizio,annoInizio,meseFine,annoFine),
                      new HorizontalLayout(numero,importo,numeroTotaleRiviste, statoRivista)
                      );
 
        getBinder()
        .forField(importo).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(RivistaAbbonamento::getImporto,RivistaAbbonamento::setImporto);
  
        getBinder()
            .forField(numero)
            .withConverter(new StringToIntegerConverter("Deve essere un numero"))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(RivistaAbbonamento::getNumero, RivistaAbbonamento::setNumero);

        getBinder()
        .forField(numeroTotaleRiviste)
        .withConverter(new StringToIntegerConverter("Deve essere un numero"))
        .withValidator(num -> num != null && num >= 0,"essere maggiore o uguale 0")
        .bind(RivistaAbbonamento::getNumeroTotaleRiviste, RivistaAbbonamento::setNumeroTotaleRiviste);

        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(RivistaAbbonamento::getPubblicazione,RivistaAbbonamento::setPubblicazione);

        getBinder()
        .forField(destinatario)
        .asRequired()
        .withValidator(p -> p != null, "Destinatario deve essere selezionato")
        .bind(RivistaAbbonamento::getDestinatario,RivistaAbbonamento::setDestinatario);

        getBinder().forField(tipoAbbonamentoRivista)
        .asRequired().bind(RivistaAbbonamento::getTipoAbbonamentoRivista,RivistaAbbonamento::setTipoAbbonamentoRivista);

        getBinder().forField(invioSpedizione)
        .asRequired().bind(RivistaAbbonamento::getInvioSpedizione,RivistaAbbonamento::setInvioSpedizione);

        getBinder().forField(meseInizio)
        .asRequired().bind(RivistaAbbonamento::getMeseInizio,RivistaAbbonamento::setMeseInizio);
        getBinder().forField(meseFine)
        .asRequired().bind(RivistaAbbonamento::getMeseFine,RivistaAbbonamento::setMeseFine);
        
        getBinder().forField(annoInizio)
        .asRequired().bind(RivistaAbbonamento::getAnnoInizio,RivistaAbbonamento::setAnnoInizio);

        getBinder().forField(annoFine)
        .asRequired().bind(RivistaAbbonamento::getAnnoFine,RivistaAbbonamento::setAnnoFine);

        getBinder().forField(statoRivista)
        .asRequired().bind(RivistaAbbonamento::getStatoRivista,RivistaAbbonamento::setStatoRivista);

        importo.setReadOnly(true);
        numeroTotaleRiviste.setReadOnly(true);
        
    }

    @Override
    public void focus(boolean persisted, RivistaAbbonamento obj) {
        pubblicazione.setReadOnly(persisted);
        destinatario.setReadOnly(persisted);
        invioSpedizione.setReadOnly(persisted);
        numero.setReadOnly(obj.getStorico() != null);
        tipoAbbonamentoRivista.setReadOnly(obj.getStorico() != null);
        meseInizio.setReadOnly(persisted);
        meseFine.setReadOnly(persisted);
        annoInizio.setReadOnly(persisted);
        annoFine.setReadOnly(persisted);        
        numeroTotaleRiviste.setVisible(persisted);
        importo.setVisible(persisted);
        pubblicazione.focus();
    }
    
}
