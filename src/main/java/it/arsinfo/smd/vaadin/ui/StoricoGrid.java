package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.vaadin.model.SmdGrid;


public class StoricoGrid extends SmdGrid<Storico> {


    public StoricoGrid(String gridname) {
        super(new Grid<>(Storico.class),gridname);

        setColumns("numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","invio");
        setColumnCaption("captionIntestatario", "Intestatario");
        setColumnCaption("captionDestinatario", "Destinatario");
        setColumnCaption("captionPubblicazione", "Pubblicazione");

    }
    
}
