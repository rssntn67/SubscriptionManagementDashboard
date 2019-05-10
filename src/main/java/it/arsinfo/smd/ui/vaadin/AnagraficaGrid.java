package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Anagrafica;

public class AnagraficaGrid extends SmdGrid<Anagrafica> {

    public AnagraficaGrid(String gridName) {
        super(new Grid<>(Anagrafica.class),gridName);

        setColumns("nome", "cognome", "diocesi.details", 
                   "citta", "provincia","cap", "paese");
        setColumnCaption("diocesi.details", "Diocesi");
    }

}
