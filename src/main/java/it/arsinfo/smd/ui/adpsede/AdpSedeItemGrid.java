package it.arsinfo.smd.ui.adpsede;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class AdpSedeItemGrid extends SmdGrid<SpedizioneDto> {

    public AdpSedeItemGrid(String gridname) {
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
        		"paese"
           );
        setColumnCaption("spedCaption","Spedizione");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("numero","Quantità");
    }

}
