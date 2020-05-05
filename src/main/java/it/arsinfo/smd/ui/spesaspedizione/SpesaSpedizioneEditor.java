package it.arsinfo.smd.ui.spesaspedizione;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.SpesaSpedizioneDao;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.vaadin.SmdEditor;

public class SpesaSpedizioneEditor
        extends SmdEditor<SpesaSpedizione> {

    private final ComboBox<AreaSpedizione> areaSpedizione = new ComboBox<AreaSpedizione>("AreaSpedizione",
                                                                    EnumSet.allOf(AreaSpedizione.class));

    private final ComboBox<RangeSpeseSpedizione> range = new ComboBox<RangeSpeseSpedizione>("Range",
            EnumSet.allOf(RangeSpeseSpedizione.class));

 
    private final TextField spese = new TextField("Spese Spedizione Posta Ordinaria");
    private final TextField cor24h = new TextField("Spese Spedizione Corriere 24h");
    private final TextField cor3gg = new TextField("Spese Spedizione Corriere 3gg");

    public SpesaSpedizioneEditor(
            SpesaSpedizioneDao spesaSpedizioneDao) {

        super(spesaSpedizioneDao, new Binder<>(SpesaSpedizione.class) );

        range.setEmptySelectionAllowed(false);
        range.setPlaceholder("Range");

        areaSpedizione.setEmptySelectionAllowed(false);
        areaSpedizione.setPlaceholder("Area Spedizione");
        
        setComponents(getActions(), 
                      new HorizontalLayout(range,areaSpedizione,spese,cor3gg,cor24h)
                      );
 
        getBinder()
        .forField(range)
        .asRequired()
        .withValidator(p -> p != null, "Range deve essere selezionato")
        .bind(SpesaSpedizione::getRangeSpeseSpedizione,SpesaSpedizione::setRangeSpeseSpedizione);
       
        getBinder()
        .forField(spese).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(SpesaSpedizione::getSpese,SpesaSpedizione::setSpese);

        getBinder()
        .forField(cor24h).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(SpesaSpedizione::getCor24h,SpesaSpedizione::setCor24h);

        getBinder()
        .forField(cor3gg).withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(bdec -> bdec != null && bdec.signum() >= 0,"deve essere maggiore di 0")
        .bind(SpesaSpedizione::getCor3gg,SpesaSpedizione::setCor3gg);


        getBinder()
        .forField(areaSpedizione)
        .asRequired()
        .withValidator(p -> p != null, "Area Spedizione deve essere selezionato")
        .bind(SpesaSpedizione::getAreaSpedizione,SpesaSpedizione::setAreaSpedizione);
        
    }

    @Override
    public void focus(boolean persisted, SpesaSpedizione obj) {
        spese.focus();        
    }

}
