package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class OperazioneGrid extends SmdGrid<Operazione> {

    private final FooterRow gridfooter;

    public OperazioneGrid(String gridName) {
        super(new Grid<>(Operazione.class), gridName);
        setColumns("pubblicazione.nome", "stimato","definitivo","mese","anno");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        gridfooter = getGrid().prependFooterRow();
    }

    @Override
    public void populate(List<Operazione> items) {
        super.populate(items);
        gridfooter.getCell("pubblicazione.nome").setHtml("<strong> Totali:</strong>");
        gridfooter.getCell("stimato").setHtml("<b>"+getTotaleStimato(items).toString()+"</b>");
        gridfooter.getCell("definitivo").setHtml("<b>"+getTotaleDefinitivo(items).toString()+"</b>");
        gridfooter.getCell("mese").setHtml("-------");
        gridfooter.getCell("anno").setHtml("-------");

    }

    private Integer getTotaleDefinitivo(List<Operazione> items) {
        return items.stream().filter(o -> o.getDefinitivo() != null).mapToInt(o -> o.getDefinitivo()).sum();
    }

    private Integer getTotaleStimato(List<Operazione> items) {
        return items.stream().filter(o -> o.getStimato() != null).mapToInt(o -> o.getStimato()).sum();
    }
}
