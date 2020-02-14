package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.dto.SpedizioniereItem;

public class SpedizioniereGrid extends SmdGrid<SpedizioniereItem> {

    public SpedizioniereGrid(String gridname) {
        super(new Grid<>(SpedizioniereItem.class),gridname);
        setColumns(
        		"spedCaption",
    			"numero",
                "caption",
                "intestazione",
                "sottoIntestazione",
                "indirizzo",
                "citta",
                "cap",
                "provincia",
        		"paese"
           );
        setColumnCaption("spedCaption","Spedizione");
        setColumnCaption("caption","Pubblicazione");
        setColumnCaption("numero","Quantità");
    }

}
