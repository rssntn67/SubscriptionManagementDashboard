package it.arsinfo.smd.ui.campagna;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class CampagnaGrid extends SmdGrid<Campagna> {

    public CampagnaGrid(String gridname) {
        super(new Grid<>(Campagna.class),gridname);

        setColumns("anno.anno","statoCampagna");
        

    }
}
