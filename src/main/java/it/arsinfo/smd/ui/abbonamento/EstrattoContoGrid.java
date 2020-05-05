package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class EstrattoContoGrid extends SmdGrid<EstrattoConto> {

    public EstrattoContoGrid(String gridname) {
        super(new Grid<>(EstrattoConto.class),gridname);
        setColumns("numeroTotaleRiviste","numero","pubblicazione.nome","beneficiario","importo",
                   "meseInizio","annoInizio","meseFine","annoFine","tipoEstrattoConto");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("numero","Quan.tà");

    }

}
