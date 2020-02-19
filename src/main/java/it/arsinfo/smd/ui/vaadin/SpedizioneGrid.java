package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        setColumns("destinazione",
        		   "meseSpedizione",
        		   "annoSpedizione", 
        		   "pesoStimato",
                   "spesePostali",
                   "invioSpedizione",
                   "statoSpedizione"
                   );
        setColumnCaption("statoSpedizione","Stato");
        setColumnCaption("invioSpedizione","Invio");
    }
}
