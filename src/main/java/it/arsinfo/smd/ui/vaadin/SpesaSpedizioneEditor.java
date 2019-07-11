package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;

public class SpesaSpedizioneEditor
        extends SmdEditor<SpesaSpedizione> {

    private final ComboBox<AreaSpedizione> areaSpedizione = new ComboBox<AreaSpedizione>("AreaSpedizione",
                                                                    EnumSet.allOf(AreaSpedizione.class));

    private final ComboBox<RangeSpeseSpedizione> range = new ComboBox<RangeSpeseSpedizione>("Range",
            EnumSet.allOf(RangeSpeseSpedizione.class));

 
    private final TextField speseSpedizione = new TextField("Spese Spedizione");

    public SpesaSpedizioneEditor(
            SpesaSpedizioneDao spesaSpedizioneDao) {

        super(spesaSpedizioneDao, new Binder<>(SpesaSpedizione.class) );

        range.setEmptySelectionAllowed(false);
        range.setPlaceholder("Range");

        areaSpedizione.setEmptySelectionAllowed(false);
        areaSpedizione.setPlaceholder("Area Spedizione");
        
        setComponents(getActions(), 
                      new HorizontalLayout(range,areaSpedizione,speseSpedizione)
                      );
 
        getBinder()
        .forField(range)
        .asRequired()
        .withValidator(p -> p != null, "Range deve essere selezionato")
        .bind(SpesaSpedizione::getRange,SpesaSpedizione::setRange);

        
        getBinder()
        .forField(speseSpedizione).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(SpesaSpedizione::getSpese,SpesaSpedizione::setSpese);
 

        getBinder()
        .forField(areaSpedizione)
        .asRequired()
        .withValidator(p -> p != null, "Area Spedizione deve essere selezionato")
        .bind(SpesaSpedizione::getArea,SpesaSpedizione::setArea);
        
    }

    @Override
    public void focus(boolean persisted, SpesaSpedizione obj) {
        speseSpedizione.focus();        
    }

}
