package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;

public class SpesaSpedizioneEditor
        extends SmdEditor<SpesaSpedizione> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazione");
    private final ComboBox<AreaSpedizione> areaSpedizione = new ComboBox<AreaSpedizione>("AreaSpedizione",
                                                                    EnumSet.allOf(AreaSpedizione.class));

    private final TextField numero = new TextField("Quant.");

 
    private final TextField speseSpedizione = new TextField("Spese Spedizione");

    public SpesaSpedizioneEditor(
            SpesaSpedizioneDao spesaSpedizioneDao,
            List<Pubblicazione> pubblicazioni) {

        super(spesaSpedizioneDao, new Binder<>(SpesaSpedizione.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);

        areaSpedizione.setEmptySelectionAllowed(false);
        areaSpedizione.setPlaceholder("Area Spedizione");
        
        setComponents(getActions(), 
                      new HorizontalLayout(pubblicazione,numero,areaSpedizione,speseSpedizione)
                      );
 
 
        getBinder()
        .forField(speseSpedizione).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(SpesaSpedizione::getSpeseSpedizione,SpesaSpedizione::setSpeseSpedizione);
 
        getBinder()
            .forField(numero)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(SpesaSpedizione::getNumero, SpesaSpedizione::setNumero);
        
        getBinder()
        .forField(areaSpedizione)
        .asRequired()
        .withValidator(p -> p != null, "Destinatario deve essere selezionato")
        .bind(SpesaSpedizione::getAreaSpedizione,SpesaSpedizione::setAreaSpedizione);

 
        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(SpesaSpedizione::getPubblicazione,SpesaSpedizione::setPubblicazione);
        
    }

    @Override
    public void focus(boolean persisted, SpesaSpedizione obj) {
        areaSpedizione.setReadOnly(persisted);
        pubblicazione.setReadOnly(persisted);
        numero.setReadOnly(persisted);
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
        }        
        speseSpedizione.focus();        
    }

}
