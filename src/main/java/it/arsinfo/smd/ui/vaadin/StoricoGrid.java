package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Storico;


public class StoricoGrid extends SmdGrid<Storico> {


    public StoricoGrid(String gridname) {
        super(new Grid<>(Storico.class),gridname);

        setColumns("numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","invio","sospeso","cassa");
        setColumnCaption("captionIntestatario", "Intestatario");
        setColumnCaption("captionDestinatario", "Destinatario");
        setColumnCaption("captionPubblicazione", "Pubblicazione");

    }
    
}
