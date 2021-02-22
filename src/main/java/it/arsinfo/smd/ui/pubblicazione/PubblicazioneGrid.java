package it.arsinfo.smd.ui.pubblicazione;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class PubblicazioneGrid extends SmdGrid<Pubblicazione> {

    public PubblicazioneGrid(String gridname) {
        super(new Grid<>(Pubblicazione.class),gridname);
        setColumns("nome", "autore","decodeAttivo","editore","tipo.descrizione",  "abbonamento",
                   "pubblicato");
        getGrid().addColumn("costoUnitario", new NumberRenderer(NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"))));
        setColumnCaption("decodeAttivo", "Attiva");

    }
    
    public static Locale getLocalFromISO(String iso4217code){
        Locale toReturn = null;
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).
                    getCurrency().getCurrencyCode();
            if (iso4217code.equals(code)) {
                toReturn = locale;
                break;
            }
        }
        return toReturn;
    }
}
