package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class RivistaAbbonamentoGrid extends SmdGrid<RivistaAbbonamento> {

    public RivistaAbbonamentoGrid(String gridname) {
        super(new Grid<>(RivistaAbbonamento.class),gridname);
        setColumns("numeroTotaleRiviste","numero","pubblicazione.nome","beneficiario","importo",
                   "meseInizio","annoInizio","meseFine","annoFine","tipoAbbonamentoRivista","statoRivista");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("numero","Quan.tà");

    }

}
