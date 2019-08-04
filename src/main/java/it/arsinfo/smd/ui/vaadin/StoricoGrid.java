package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Storico;


public class StoricoGrid extends SmdGrid<Storico> {

    private final FooterRow gridfooter;

    public StoricoGrid(String gridname) {
        super(new Grid<>(Storico.class),gridname);

        setColumns("numero","captionPubblicazione","captionIntestatario","captionDestinatario","tipoEstrattoConto","invioSpedizione","invio","statoStorico","cassa");
        setColumnCaption("captionIntestatario", "Intestatario");
        setColumnCaption("captionDestinatario", "Destinatario");
        setColumnCaption("captionPubblicazione", "Pubblicazione");
        setColumnCaption("invioSpedizione", "Sped.");
        setColumnCaption("statoStorico", "Stato");
        gridfooter = getGrid().prependFooterRow();

    }
    
    @Override
    public void populate(List<Storico> storici) {
        super.populate(storici);
        gridfooter.getCell("numero").setHtml("<b>Totale Abbonamenti: "+getTotale(storici).toString()+"</b>");

    }

    private Integer getTotale(List<Storico> storici) {
        Integer totale = 0;
        for (Storico storico:storici) {
            totale+=storico.getNumero();
        }
        return totale;
    }
    
}
