package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Spedizione;

public class SpedizioneGrid extends SmdGrid<Spedizione> {

    public SpedizioneGrid(String gridname) {
        super(new Grid<>(Spedizione.class),gridname);
        setColumns("numero","pubblicazione.nome",
                   "intestazione","sottoIntestazione","indirizzo","citta"
                   ,"cap","provincia","paese","abbonamento.intestatario.caption","destinatario.caption","decodeSospesa","omaggio","invioSpedizione","invio","abbonamento.anno");
        setColumnCaption("abbonamento.intestatario.caption","Intestatario");
        setColumnCaption("destinatario.caption","Destinatario");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("decodeSospesa","Sospesa");
        setColumnCaption("invioSpedizione","Sped.");
        setColumnCaption("numero","Quan.tà");

    }

}
