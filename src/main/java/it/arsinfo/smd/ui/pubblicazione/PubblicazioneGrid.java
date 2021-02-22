package it.arsinfo.smd.ui.pubblicazione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class PubblicazioneGrid extends SmdGrid<Pubblicazione> {

    public PubblicazioneGrid(String gridname) {
        super(new Grid<>(Pubblicazione.class),gridname);
        getGrid().addColumn("nome");
        getGrid().addColumn("autore");
        getGrid().addColumn("decodeAttivo").setCaption("Attiva");
        getGrid().addColumn("editore");
        getGrid().addColumn("tipo.descrizione");
        getGrid().addColumn("abbonamento", EuroConverter.getEuroRenderer()).setCaption("importo abbonamento");
        getGrid().addColumn("costoUnitario", EuroConverter.getEuroRenderer()).setCaption("costo singola");
        getGrid().addColumn("pubblicato");
        getGrid().setColumnOrder(
        		"nome", 
        		"tipo.descrizione",
                "pubblicato",
        		"abbonamento",
        		"costoUnitario",
        		"decodeAttivo",
        		"autore",
        		"editore");

    }
    
}
