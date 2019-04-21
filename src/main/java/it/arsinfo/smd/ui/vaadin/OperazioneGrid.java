package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Operazione;

public class OperazioneGrid extends SmdGrid<Operazione> {

    private final FooterRow gridfooter;

    public OperazioneGrid(String gridName) {
        super(new Grid<>(Operazione.class), gridName);
        setColumns("pubblicazione.nome", "stimato","definitivo","mese","anno","invioSpedizione");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        setColumnCaption("invioSpedizione", "Sped.");
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
