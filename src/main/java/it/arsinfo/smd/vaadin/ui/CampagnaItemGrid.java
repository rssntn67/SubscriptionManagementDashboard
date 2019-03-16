package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class CampagnaItemGrid extends SmdGrid<CampagnaItem> {

    public CampagnaItemGrid(String gridname) {
        super(new Grid<>(CampagnaItem.class),gridname);
        setColumns("campagna.anno","pubblicazione.caption");
        setColumnCaption("pubblicazione.caption","Pubblicazione");
    }

}
