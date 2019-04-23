package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Abbonamento;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    private final FooterRow gridfooter;

    public AbbonamentoGrid(String gridName) {
        super(new Grid<>(Abbonamento.class),gridName);
        setColumns("intestatario.caption","campo","totale","costo", "spese","incassato","cassa", "anno", "inizio","fine","campagnaAsString");
        setColumnCaption("intestatario.caption", "Intestatario");
        setColumnCaption("campagnaAsString", "Campagna");
        gridfooter = getGrid().prependFooterRow();
    }
    @Override
    public void populate(List<Abbonamento> items) {
        super.populate(items);
        gridfooter.getCell("campo").setHtml("<strong>Importo Totale:</strong>");
        gridfooter.getCell("totale").setHtml("<b>"+getTotale(items).toString()+"</b>");
        gridfooter.getCell("costo").setHtml("<b>"+getCosto(items).toString()+"</b>");
        gridfooter.getCell("spese").setHtml("<b>"+getSpese(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassato(items).toString()+"</b>");
        gridfooter.getCell("cassa").setHtml("-------");

    }
    
    private BigDecimal getTotale(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getTotale());
        }
        return importo;
    }

    private BigDecimal getIncassato(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abb:abbonamenti) {
            if (abb.getVersamento() != null)
                importo=importo.add(abb.getTotale());
        }
        return importo;
    }

    private BigDecimal getCosto(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getCosto());
        }
        return importo;
    }

    private BigDecimal getSpese(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getSpese());
        }
        return importo;
    }

}
