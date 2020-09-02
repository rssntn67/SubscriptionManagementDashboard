package it.arsinfo.smd.ui.spedizione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        setColumns("destinazione",
        		   "meseSpedizione",
        		   "annoSpedizione", 
        		   "pesoStimato",
                   "spesePostali",
                   "invioSpedizione"
                   );
        setColumnCaption("invioSpedizione","Invio");
    }
}
