package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.EstrattoConto;

public class EstrattoContoGrid extends SmdGrid<EstrattoConto> {

    public EstrattoContoGrid(String gridname) {
        super(new Grid<>(EstrattoConto.class),gridname);
        setColumns("numeroTotaleRiviste","numero","pubblicazione.nome","destinatario.denominazione","importo",
                   "meseInizio","annoInizio","meseFine","annoFine","tipoEstrattoConto");
        setColumnCaption("abbonamento.intestatario.denominazione","Intestatario");
        setColumnCaption("destinatario.denominazione","Destinatario");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("numero","Quan.tà");

    }

}
