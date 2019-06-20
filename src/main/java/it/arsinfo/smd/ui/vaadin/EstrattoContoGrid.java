package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.EstrattoConto;

public class EstrattoContoGrid extends SmdGrid<EstrattoConto> {

    public EstrattoContoGrid(String gridname) {
        super(new Grid<>(EstrattoConto.class),gridname);
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
