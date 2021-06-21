package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.NumberRenderer;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.ui.entity.EntityGridView;

public abstract class RivistaAbbonamentoGrid extends EntityGridView<RivistaAbbonamento> {


    @Override
    public void init(Grid<RivistaAbbonamento> grid) {
        super.init(grid);
        configureGrid("beneficiario");
        grid.addColumn("pubblicazione.nome").setHeader("Pubblicazione");
        grid.addColumn("inizio");
        grid.addColumn("fine");
        grid.addColumn("tipoAbbonamentoRivista").setHeader("Abbonamento");
        grid.addColumn("statoRivista").setHeader("Stato");
        grid.addColumn("numero");
        grid.addColumn("numeroTotaleRiviste");
        grid.addColumn(new NumberRenderer<>(RivistaAbbonamento::getImporto, Smd.getEuroCurrency())).setHeader("Importo");

    }

}
