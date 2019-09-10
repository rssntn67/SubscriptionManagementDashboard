package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Anagrafica;

public class AnagraficaGrid extends SmdGrid<Anagrafica> {

    public AnagraficaGrid(String gridName) {
        super(new Grid<>(Anagrafica.class),gridName);

        setColumns("titolo","denominazione", "nome", "diocesi.details", 
                   "citta", "provincia","cap", "paese.nome");
        setColumnCaption("diocesi.details", "Diocesi");
        setColumnCaption("paese.nome", "Paese");
    }

}
