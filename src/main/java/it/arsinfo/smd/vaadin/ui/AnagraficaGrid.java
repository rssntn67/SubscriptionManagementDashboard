package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class AnagraficaGrid extends SmdGrid<Anagrafica> {

    public AnagraficaGrid(String gridName) {
        super(new Grid<>(Anagrafica.class),gridName);

        setColumns("nome", "cognome", "diocesi.details", 
                   "citta", "cap", "paese");
        setColumnCaption("diocesi.details", "Diocesi");
    }

}
