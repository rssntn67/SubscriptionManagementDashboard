package it.arsinfo.smd.ui.dto;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpedizioneDtoGrid extends SmdGrid<SpedizioneDto> {

    public SpedizioneDtoGrid(String gridname) {
        super(new Grid<>(SpedizioneDto.class),gridname);
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
        		"paese",
        		"omaggio"
           );
        setColumnCaption("spedCaption","Spedizione");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("numero","Quantità");
    }

}
