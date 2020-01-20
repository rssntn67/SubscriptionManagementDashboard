package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneItemGrid2 extends SmdGrid<SpedizioneItem> {

    public SpedizioneItemGrid2(String gridname) {
        super(new Grid<>(SpedizioneItem.class),gridname);
        setColumns("spedCaption",
        			"numero",
                   "caption",
                   "intestazione",
                   "sottoIntestazione",
                   "indirizzo",
                   "citta"
                   ,"cap",
                   "provincia",
                   "paese");
        setColumnCaption("spedCaption","Spedizione");
        setColumnCaption("caption","Pubblicazione");
        setColumnCaption("numero","Quantità");
    }

}
