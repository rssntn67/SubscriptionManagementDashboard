package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class VersamentoGrid extends SmdGrid<Versamento> {

    public VersamentoGrid() {
        super(new Grid<>(Versamento.class));
        setColumns("ccp.ccp","campo","campovalido",              
                        "dataPagamento","dataContabile","importo",
                        "errore",
                        "tipoDocumento.bollettino",
                        "tipoAccettazione.tipo","tipoSostitutivo.descr",
                        "provincia","ufficio","sportello","bobina", "progressivoBobina"
                      );
        
    }

}
