package it.arsinfo.smd.ui.abbonamento;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    private final FooterRow gridfooter;

    public AbbonamentoGrid(String gridName) {
        super(new Grid<>(Abbonamento.class),gridName);
    	setColumns("intestazione",
        		"importo",
        		"spese",
        		"speseEstero",
        		"speseEstrattoConto",
        		"pregresso",
        		"totale",
        		"incassato",
        		"residuo",
        		"anno");
        gridfooter = getGrid().prependFooterRow();
	}
    
    @Override
    public void populate(List<Abbonamento> items) {
        super.populate(items);
        gridfooter.getCell("spese").setHtml("<b>"+getSpese(items).toString()+"</b>");
        gridfooter.getCell("speseEstero").setHtml("<b>"+getSpeseEstero(items).toString()+"</b>");
        gridfooter.getCell("speseEstrattoConto").setHtml("<b>"+getSpeseEstrattoConto(items).toString()+"</b>");
        gridfooter.getCell("importo").setHtml("<b>"+getImporto(items).toString()+"</b>");
        gridfooter.getCell("pregresso").setHtml("<b>"+getPregresso(items).toString()+"</b>");
        gridfooter.getCell("totale").setHtml("<b>"+getTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassato(items).toString()+"</b>");
        gridfooter.getCell("residuo").setHtml("<b>"+getResiduo(items).toString()+"</b>");
 
    }
    private BigDecimal getPregresso(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregresso());
        }
        return importo;
    }

    private BigDecimal getImporto(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImporto());
        }
        return importo;
    }

    private BigDecimal getSpese(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpese());
        }
        return importo;
    }

    private BigDecimal getSpeseEstero(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstero());
        }
        return importo;
    }

    private BigDecimal getSpeseEstrattoConto(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoConto());
        }
        return importo;
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
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassato());
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
