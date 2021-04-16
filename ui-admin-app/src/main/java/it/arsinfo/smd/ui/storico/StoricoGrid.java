package it.arsinfo.smd.ui.storico;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdGrid;


public class StoricoGrid extends SmdGrid<Storico> {

    private final FooterRow gridfooter;

    public StoricoGrid(String gridname) {
        super(new Grid<>(Storico.class),gridname);

        setColumns(
        		"intestazione",
        		"beneficiario",
        		"tipoAbbonamentoRivista",
        		"captionPubblicazione",
        		"numero",
        		"invioSpedizione",
        		"statoStorico",
        		"contrassegno");
        setColumnCaption("captionPubblicazione", "Pubblicazione");
        setColumnCaption("invioSpedizione", "Sped.");
        setColumnCaption("statoStorico", "Stato");
        gridfooter = getGrid().prependFooterRow();

    }
    
    @Override
    public void populate(List<Storico> storici) {
        super.populate(storici);
        gridfooter.getCell("intestazione").setHtml("<b>Riviste in Abbonamento: "+getTotale(storici).toString()+"</b>");
    }

    private Integer getTotale(List<Storico> storici) {
        Integer totale = 0;
        for (Storico storico:storici) {
            totale+=storico.getNumero();
        }
        return totale;
    }
    
}
