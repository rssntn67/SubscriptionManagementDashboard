package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Operazione;

public class OperazioneGrid extends SmdGrid<Operazione> {

    private final FooterRow gridfooter;

    public OperazioneGrid(String gridName) {
        super(new Grid<>(Operazione.class), gridName);
        setColumns("mese","anno","pubblicazione.nome", "mesePubblicazione","annoPubblicazione",
                   "stimatoSped","stimatoSede","totaleStimato",
                   "definitivoSped","definitivoSede","totaleDefinitivo");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        gridfooter = getGrid().prependFooterRow();
    }

    @Override
    public void populate(List<Operazione> items) {
        super.populate(items);
        gridfooter.getCell("pubblicazione.nome").setHtml("<strong> Totali:</strong>");
        gridfooter.getCell("stimatoSped").setHtml("<b>"+getStimatoSped(items).toString()+"</b>");
        gridfooter.getCell("stimatoSede").setHtml("<b>"+getStimatoSede(items).toString()+"</b>");
        gridfooter.getCell("totaleStimato").setHtml("<b>"+getStimato(items).toString()+"</b>");
        gridfooter.getCell("definitivoSped").setHtml("<b>"+getDefSped(items).toString()+"</b>");
        gridfooter.getCell("definitivoSede").setHtml("<b>"+getDefSede(items).toString()+"</b>");
        gridfooter.getCell("totaleDefinitivo").setHtml("<b>"+getDefinitivo(items).toString()+"</b>");
 
    }

    private Integer getDefinitivo(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getTotaleDefinitivo()).sum();
    }

    private Integer getStimato(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getTotaleStimato()).sum();
    }
    private Integer getStimatoSped(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getStimatoSped()).sum();
    }
    private Integer getStimatoSede(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getStimatoSede()).sum();
    }
    private Integer getDefSped(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getDefinitivoSped()).sum();
    }
    private Integer getDefSede(List<Operazione> items) {
        return items.stream().mapToInt(o -> o.getDefinitivoSede()).sum();
    }

    
}
