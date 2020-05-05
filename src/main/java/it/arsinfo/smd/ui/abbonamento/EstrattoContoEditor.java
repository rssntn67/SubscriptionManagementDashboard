package it.arsinfo.smd.ui.abbonamento;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.EstrattoContoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdEditor;

public class EstrattoContoEditor
        extends SmdEditor<EstrattoConto> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazione");
    
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<TipoEstrattoConto> tipoEstrattoconto = new ComboBox<TipoEstrattoConto>("Tipo",
                                                                    EnumSet.allOf(TipoEstrattoConto.class));
    
    private final ComboBox<Anno> annoInizio = new ComboBox<Anno>("Anno Inizio",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Anno> annoFine = new ComboBox<Anno>("Anno Fine",
            EnumSet.allOf(Anno.class));

    private final ComboBox<Mese> meseInizio = new ComboBox<Mese>("Mese Inizio",
            EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> meseFine = new ComboBox<Mese>("Mese Fine",
            EnumSet.allOf(Mese.class));

    private final TextField numeroTotaleRiviste = new TextField("Numero Totale Riviste");
    private final TextField numero = new TextField("Quant.");

    private final TextField importo = new TextField("Importo");

    
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
            EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));

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
        invioSpedizione.setEmptySelectionAllowed(false);

        tipoEstrattoconto.setEmptySelectionAllowed(false);

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
        
        setComponents(getActions(), 
                      new HorizontalLayout(pubblicazione,destinatario,importo,numero,numeroTotaleRiviste),
                      new HorizontalLayout(invio,invioSpedizione,tipoEstrattoconto),
                      new HorizontalLayout(meseInizio,annoInizio,meseFine,annoFine)
                      );
 
        getBinder()
        .forField(importo).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(EstrattoConto::getImporto,EstrattoConto::setImporto);
  
        getBinder()
            .forField(numero)
            .withConverter(new StringToIntegerConverter("Deve essere un numero"))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(EstrattoConto::getNumero, EstrattoConto::setNumero);

        getBinder()
        .forField(numeroTotaleRiviste)
        .withConverter(new StringToIntegerConverter("Deve essere un numero"))
        .withValidator(num -> num != null && num >= 0,"essere maggiore o uguale 0")
        .bind(EstrattoConto::getNumeroTotaleRiviste, EstrattoConto::setNumeroTotaleRiviste);

        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(EstrattoConto::getPubblicazione,EstrattoConto::setPubblicazione);

        getBinder()
        .forField(destinatario)
        .asRequired()
        .withValidator(p -> p != null, "Destinatario deve essere selezionato")
        .bind(EstrattoConto::getDestinatario,EstrattoConto::setDestinatario);

        getBinder().forField(tipoEstrattoconto)
        .asRequired().bind(EstrattoConto::getTipoEstrattoConto,EstrattoConto::setTipoEstrattoConto);

        getBinder().forField(invio)
        .asRequired().bind(EstrattoConto::getInvio,EstrattoConto::setInvio);

        getBinder().forField(invioSpedizione)
        .asRequired().bind(EstrattoConto::getInvioSpedizione,EstrattoConto::setInvioSpedizione);

        getBinder().forField(meseInizio)
        .asRequired().bind(EstrattoConto::getMeseInizio,EstrattoConto::setMeseInizio);
        getBinder().forField(meseFine)
        .asRequired().bind(EstrattoConto::getMeseFine,EstrattoConto::setMeseFine);
        
        getBinder().forField(annoInizio)
        .asRequired().bind(EstrattoConto::getAnnoInizio,EstrattoConto::setAnnoInizio);

        getBinder().forField(annoFine)
        .asRequired().bind(EstrattoConto::getAnnoFine,EstrattoConto::setAnnoFine);
        
        importo.setReadOnly(true);
        numeroTotaleRiviste.setReadOnly(true);
        
    }

    @Override
    public void focus(boolean persisted, EstrattoConto obj) {
        pubblicazione.setReadOnly(persisted);
        destinatario.setReadOnly(persisted);
        invio.setReadOnly(persisted);
        invioSpedizione.setReadOnly(persisted);
        numero.setReadOnly(obj.getStorico() != null);
        tipoEstrattoconto.setReadOnly(obj.getStorico() != null);
        meseInizio.setReadOnly(persisted);
        meseFine.setReadOnly(persisted);
        annoInizio.setReadOnly(persisted);
        annoFine.setReadOnly(persisted);
        
        getCancel().setEnabled(obj.getStorico() == null);
        getDelete().setEnabled(obj.getStorico() == null);
        getSave().setEnabled(obj.getStorico() == null);
        
        numero.focus();
    }
    
}
