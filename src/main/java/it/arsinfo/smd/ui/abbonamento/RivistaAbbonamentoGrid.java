package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class RivistaAbbonamentoGrid extends SmdGrid<RivistaAbbonamento> {

    public RivistaAbbonamentoGrid(String gridname) {
        super(new Grid<>(RivistaAbbonamento.class),gridname);
        
        getGrid().addColumn("pubblicazione.nome").setCaption("Pubblicazione");
        getGrid().addColumn("inizio");
        getGrid().addColumn("fine");
        getGrid().addColumn("tipoAbbonamentoRivista").setCaption("Abbonamento");
        getGrid().addColumn("beneficiario");
        getGrid().addColumn("statoRivista");
        getGrid().addColumn("numero").setCaption("Quan.tà");
        getGrid().addColumn("numeroTotaleRiviste");
        getGrid().addColumn("importo", EuroConverter.getEuroRenderer());

        getGrid().setColumnOrder(
    		"pubblicazione.nome",
            "inizio",
            "fine",
            "tipoAbbonamentoRivista",
    		"beneficiario",
            "statoRivista",
            "numero",
    		"numeroTotaleRiviste",
    		"importo")
        ;

    }

}
