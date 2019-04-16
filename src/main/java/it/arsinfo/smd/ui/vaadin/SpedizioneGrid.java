package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        setColumns("destinatario.caption","pubblicazione.caption","numero","omaggio","invio");
        setColumnCaption("destinatario.caption","Destinatario");
        setColumnCaption("pubblicazione.caption","Pubblicazione");

    }

}
