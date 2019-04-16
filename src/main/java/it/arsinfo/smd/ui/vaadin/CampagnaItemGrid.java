package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.CampagnaItem;

public class CampagnaItemGrid extends SmdGrid<CampagnaItem> {

    public CampagnaItemGrid(String gridname) {
        super(new Grid<>(CampagnaItem.class),gridname);
        setColumns("campagna.anno","pubblicazione.caption");
        setColumnCaption("pubblicazione.caption","Pubblicazione");
    }

}
