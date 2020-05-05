package it.arsinfo.smd.ui.spedizioniere;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.dto.SpedizioniereItem;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpedizioniereItemGrid extends SmdGrid<SpedizioniereItem> {

    public SpedizioniereItemGrid(String gridname) {
        super(new Grid<>(SpedizioniereItem.class),gridname);
        setColumns(
        		"spedCaption",
    			"numero",
                "pubbCaption",
                "intestazione",
                "sottoIntestazione",
                "indirizzo",
                "citta",
                "cap",
                "provincia",
        		"paese"
           );
        setColumnCaption("spedCaption","Spedizione");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("numero","Quantità");
    }

}
