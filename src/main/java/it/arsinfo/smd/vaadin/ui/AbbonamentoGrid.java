package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    Grid<Abbonamento> grid;

    public AbbonamentoGrid(String gridName) {
        super(new Grid<>(Abbonamento.class),gridName);
        setColumns("intestatario.caption", "cassa", "costo", "spese","anno", "inizio","fine","data","campagnaAsString");
        setColumnCaption("intestatario.caption", "Intestatario");
        setColumnCaption("campagnaAsString", "Campagna");
    }

}
