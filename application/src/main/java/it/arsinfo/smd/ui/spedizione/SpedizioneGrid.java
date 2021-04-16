package it.arsinfo.smd.ui.spedizione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        getGrid().addColumn("destinazione");
        getGrid().addColumn("meseSpedizione.nomeBreve").setCaption("Mese");
        getGrid().addColumn("annoSpedizione.anno").setCaption("Anno");
        getGrid().addColumn("pesoStimato");
        getGrid().addColumn("spesePostali", EuroConverter.getEuroRenderer());
        getGrid().addColumn("invioSpedizione").setCaption("Invio");

    }
}
