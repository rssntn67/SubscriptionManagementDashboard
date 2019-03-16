package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class CampagnaGrid extends SmdGrid<Campagna> {

    public CampagnaGrid(String gridname) {
        super(new Grid<>(Campagna.class),gridname);

        setColumns("anno", "inizio", "fine","rinnovaSoloAbbonatiInRegola");

    }
}
