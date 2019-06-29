package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        setColumns("meseSpedizione","annoSpedizione", "numero",
                   "pubblicazione", "mesePubblicazione","annoPubblicazione",
                   "invioSpedizione","statoSpedizione",
                   "intestazione","sottoIntestazione",
                   "indirizzo","citta"
                   ,"cap","provincia","paese");
        setColumnCaption("statoSpedizione","Stato");
        setColumnCaption("invioSpedizione","Sped.");
        setColumnCaption("numero","Quan.tà");

    }

}
