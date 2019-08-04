package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneItemGrid extends SmdGrid<SpedizioneItem> {

    public SpedizioneItemGrid(String gridname) {
        super(new Grid<>(SpedizioneItem.class),gridname);
        setColumns("numero","spedCaption",
                   "caption","posticipata");
        setColumnCaption("spedCaption","Sped.");
        setColumnCaption("caption","Pubbl");
        setColumnCaption("numero","Quan.tà");
    }

}