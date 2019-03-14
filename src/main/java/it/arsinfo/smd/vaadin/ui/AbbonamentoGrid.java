package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    Grid<Abbonamento> grid;

    public AbbonamentoGrid() {
        super(new Grid<>(Abbonamento.class));
        setColumns("intestatario.caption", "cassa", "costo", "anno", "data");
        setColumnCaption("intestatario.caption", "Intestatario");
    }

}
