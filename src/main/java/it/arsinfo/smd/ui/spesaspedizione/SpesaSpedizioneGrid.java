package it.arsinfo.smd.ui.spesaspedizione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpesaSpedizioneGrid extends SmdGrid<SpesaSpedizione> {

    public SpesaSpedizioneGrid(String gridname) {
        super(new Grid<>(SpesaSpedizione.class),gridname);
        setColumns("rangeSpeseSpedizione","areaSpedizione","spese","cor24h","cor3gg");
        setColumnCaption("spese","Spese Postali");
        setColumnCaption("cor24h","Corriere 24h");
        setColumnCaption("cor3gg","Corriere 3gg");
    }

}
