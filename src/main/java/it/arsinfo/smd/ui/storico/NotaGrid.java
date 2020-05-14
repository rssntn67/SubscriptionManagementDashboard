package it.arsinfo.smd.ui.storico;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class NotaGrid extends SmdGrid<Nota> {

    public NotaGrid(String gridname) {
        super(new Grid<>(Nota.class),gridname);
        setColumns("data", "operatore",
                   "description"
                    );
    }

}
