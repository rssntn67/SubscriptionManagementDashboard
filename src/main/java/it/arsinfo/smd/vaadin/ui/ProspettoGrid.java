package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class ProspettoGrid extends SmdGrid<Prospetto> {

    private final FooterRow gridfooter;
    public ProspettoGrid(String gridName) {
        super(new Grid<>(Prospetto.class), gridName);
        setColumns("pubblicazione.nome","omaggio","stimato","mese","anno");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        gridfooter = getGrid().prependFooterRow();
    }

    @Override
    public void populate(List<Prospetto> items) {
        super.populate(items);
        gridfooter.getCell("pubblicazione.nome").setHtml("--------");
        gridfooter.getCell("omaggio").setHtml("<strong> Totali:</strong>");
        gridfooter.getCell("stimato").setHtml("<b>"+getTotaleStimato(items).toString()+"</b>");
        gridfooter.getCell("mese").setHtml("-------");
        gridfooter.getCell("anno").setHtml("-------");
    }
    
    private Integer getTotaleStimato(List<Prospetto> items) {
        Integer totale = 0;
        for (Prospetto oper:items) {
            totale=totale+(oper.getStimato());
        }
        return totale;    
    }

}
