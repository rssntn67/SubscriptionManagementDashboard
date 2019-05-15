package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Operazione;

public class OperazioneGrid extends SmdGrid<Operazione> {

    private final FooterRow gridfooter;

    public OperazioneGrid(String gridName) {
        super(new Grid<>(Operazione.class), gridName);
        setColumns("pubblicazione.nome", "sped","sede","stimato","definitivo","mese","anno");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        setColumnCaption("stimato", "Quantità");
        gridfooter = getGrid().prependFooterRow();
    }

    @Override
    public void populate(List<Operazione> items) {
        super.populate(items);
        gridfooter.getCell("pubblicazione.nome").setHtml("<strong> Totali:</strong>");
        gridfooter.getCell("sped").setHtml("<b>"+getTotaleSped(items).toString()+"</b>");
        gridfooter.getCell("sede").setHtml("<b>"+getTotaleSede(items).toString()+"</b>");
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
    private Integer getTotaleSped(List<Operazione> items) {
        return items.stream().filter(o -> o.getSped() != null).mapToInt(o -> o.getSped()).sum();
    }
    private Integer getTotaleSede(List<Operazione> items) {
        return items.stream().filter(o -> o.getSede() != null).mapToInt(o -> o.getSede()).sum();
    }
}
