package it.arsinfo.smd.ui.spesaspedizione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpesaSpedizioneGrid extends SmdGrid<SpesaSpedizione> {

    public SpesaSpedizioneGrid(String gridname) {
        super(new Grid<>(SpesaSpedizione.class),gridname);
        getGrid().addColumn("rangeSpeseSpedizione").setCaption("Peso");
        getGrid().addColumn("areaSpedizione").setCaption("Area");
        getGrid().addColumn("spese",  EuroConverter.getEuroRenderer()).setCaption("Posta Ordin.");
        getGrid().addColumn("cor24h", EuroConverter.getEuroRenderer()).setCaption("Corriere 24h");
        getGrid().addColumn("cor3gg", EuroConverter.getEuroRenderer()).setCaption("Corriere 3gg");
        
    }

}
