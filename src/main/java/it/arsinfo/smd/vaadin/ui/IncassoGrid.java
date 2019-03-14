package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class IncassoGrid extends SmdGrid<Incasso> {

    public IncassoGrid(String gridname) {
        super(new Grid<>(Incasso.class),gridname);

        setColumns("cuas.denominazione", "ccp.ccp", "dataContabile",
                   "totaleDocumenti", "totaleImporto", "documentiEsatti",
                   "importoDocumentiEsatti", "documentiErrati",
                   "importoDocumentiErrati");

    }
}
