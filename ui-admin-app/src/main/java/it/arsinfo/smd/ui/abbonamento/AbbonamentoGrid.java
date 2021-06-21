package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

import java.math.BigDecimal;
import java.util.List;

public class AbbonamentoGrid extends SmdGrid<Abbonamento> {

    private final FooterRow gridfooter;

    public AbbonamentoGrid(String gridName) {
        super(new Grid<>(Abbonamento.class),gridName);
        
        getGrid().addColumn("intestazione");
        getGrid().addColumn("importo", EuroConverter.getEuroRenderer());
        getGrid().addColumn("spese",EuroConverter.getEuroRenderer());
        getGrid().addColumn("speseEstero",EuroConverter.getEuroRenderer());
        getGrid().addColumn("speseEstrattoConto",EuroConverter.getEuroRenderer());
        getGrid().addColumn("pregresso",EuroConverter.getEuroRenderer());
        getGrid().addColumn("totale",EuroConverter.getEuroRenderer());
        getGrid().addColumn("incassato",EuroConverter.getEuroRenderer());
        getGrid().addColumn("residuo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("statoAbbonamento");

        gridfooter = getGrid().prependFooterRow();

	}
    
    @Override
    public void populate(List<Abbonamento> items) {
        super.populate(items);
        gridfooter.getCell("intestazione").setHtml("<b>Totali (EUR): </b>");
        gridfooter.getCell("spese").setHtml("<b>"+Abbonamento.getSpese(items).toString()+"</b>");
        gridfooter.getCell("speseEstero").setHtml("<b>"+Abbonamento.getSpeseEstero(items).toString()+"</b>");
        gridfooter.getCell("speseEstrattoConto").setHtml("<b>"+Abbonamento.getSpeseEstrattoConto(items).toString()+"</b>");
        gridfooter.getCell("importo").setHtml("<b>"+Abbonamento.getImporto(items).toString()+"</b>");
        gridfooter.getCell("pregresso").setHtml("<b>"+Abbonamento.getPregresso(items).toString()+"</b>");
        gridfooter.getCell("totale").setHtml("<b>"+Abbonamento.getTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+Abbonamento.getIncassato(items).toString()+"</b>");
        gridfooter.getCell("residuo").setHtml("<b>"+Abbonamento.getResiduo(items).toString()+"</b>");
 
    }

}
