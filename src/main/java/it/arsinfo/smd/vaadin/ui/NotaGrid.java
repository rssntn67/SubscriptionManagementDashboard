package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class NotaGrid extends SmdGrid<Nota> {

    public NotaGrid(String gridname) {
        super(new Grid<>(Nota.class),gridname);
        setColumns("anagrafica.cognome", "anagrafica.nome", "data",
                   "description");

    }

}
