package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class ProspettoGrid extends SmdGrid<Prospetto> {

    public ProspettoGrid(String gridName) {
        super(new Grid<>(Prospetto.class), gridName);
        setColumns("pubblicazione.caption", "anno","mese", "stimato","definitivo");
    }

}
