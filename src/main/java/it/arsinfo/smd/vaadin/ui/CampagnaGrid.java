package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class CampagnaGrid extends SmdGrid<Campagna> {

    public CampagnaGrid() {
        super(new Grid<>(Campagna.class));

        setColumns("anno", "inizio", "fine", "estratti", "blocchetti",
                   "lodare", "messaggio", "pagato", "anagraficaFlagA",
                   "anagraficaFlagB", "anagraficaFlagC");

    }
}
