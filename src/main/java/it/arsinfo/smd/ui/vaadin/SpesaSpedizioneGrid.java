package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpesaSpedizione;

public class SpesaSpedizioneGrid extends SmdGrid<SpesaSpedizione> {

    public SpesaSpedizioneGrid(String gridname) {
        super(new Grid<>(SpesaSpedizione.class),gridname);
        setColumns("range","area","spese");
    }

}
