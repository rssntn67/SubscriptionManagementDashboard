package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.vaadin.model.SmdGrid;


public class StoricoGrid extends SmdGrid<Storico> {


    public StoricoGrid() {
        super(new Grid<>(Storico.class));

        setColumns("numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","invio");
        setColumnCamptio("captionIntestatario", "Intestatario");
        setColumnCamptio("captionDestinatario", "Destinatario");
        setColumnCamptio("captionPubblicazione", "Pubblicazione");

    }
    
}
