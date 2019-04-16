package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Abbonamento;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    Grid<Abbonamento> grid;

    public AbbonamentoGrid(String gridName) {
        super(new Grid<>(Abbonamento.class),gridName);
        setColumns("intestatario.caption", "incassato","cassa", "totale","costo", "spese","anno", "inizio","fine","data","campagnaAsString");
        setColumnCaption("intestatario.caption", "Intestatario");
        setColumnCaption("campagnaAsString", "Campagna");
    }

}
