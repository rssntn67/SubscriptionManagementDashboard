package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Versamento;

public class VersamentoGrid extends SmdGrid<Versamento> {

    public VersamentoGrid(String gridname) {
        super(new Grid<>(Versamento.class),gridname);
        setColumns("incassato",
                   "importo",
                   "residuo",
                   "dataPagamento",
                   "dataContabile",
                   "progressivo",
                   "campo"
                  );
    }

}
