package it.arsinfo.smd.ui.spedizione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpedizioneItemGrid extends SmdGrid<SpedizioneItem> {

    public SpedizioneItemGrid(String gridname) {
        super(new Grid<>(SpedizioneItem.class),gridname);
        setColumns("spedCaption",
                   "pubbCaption",
                   "numero",
        		   "posticipata");
        setColumnCaption("spedCaption","Mese/Anno Spedizione");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("numero","Quan.tà");
    }

}
