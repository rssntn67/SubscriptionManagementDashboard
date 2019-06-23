package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Campagna;

public class CampagnaGrid extends SmdGrid<Campagna> {

    public CampagnaGrid(String gridname) {
        super(new Grid<>(Campagna.class),gridname);

        setColumns("anno");

    }
}
