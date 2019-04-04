package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class VersamentoGrid extends SmdGrid<Versamento> {

    public VersamentoGrid(String gridname) {
        super(new Grid<>(Versamento.class),gridname);
        setColumns("incassato",
                   "importo",
                   "residuo",
                   "progressivo",
                   "campo",
                   "dataPagamento",
                   "dataContabile"
                  );
    }

}
