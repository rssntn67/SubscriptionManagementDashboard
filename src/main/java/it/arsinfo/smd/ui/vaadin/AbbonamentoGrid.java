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
        setColumns("intestatario.caption","codeLine","importo","spese","pregresso","totale","incassato","residuo","statoAbbonamento","statoIncasso","cassa", "anno","campagnaAsString");
        setColumnCaption("intestatario.caption", "Intestatario");
        setColumnCaption("statoAbbonamento", "Stato");
        setColumnCaption("campagnaAsString", "Campagna");
        gridfooter = getGrid().prependFooterRow();
    }
    @Override
    public void populate(List<Abbonamento> items) {
        super.populate(items);
        gridfooter.getCell("totale").setHtml("<b>"+getTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassato(items).toString()+"</b>");
        gridfooter.getCell("residuo").setHtml("<b>"+getResiduo(items).toString()+"</b>");
 
    }
    
    private BigDecimal getTotale(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotale());
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

    private BigDecimal getResiduo(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getResiduo());
        }
        return importo;
    }

}
