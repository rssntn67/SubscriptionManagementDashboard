package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class VersamentoGrid extends SmdGrid<Versamento> {

    public VersamentoGrid(String gridname) {
        super(new Grid<>(Versamento.class),gridname);
        setColumns("progressivo","dataPagamento","dataContabile","importo",
                    "campo"
                      );
        
        
        /*
        setColumns("bobina", "progressivoBobina",
                   "progressivo",
                   "dataPagamento","dataContabile","importo",
                   "tipoDocumento.bollettino","provincia","ufficio","sportello",
                   "tipoAccettazione.tipo","tipoSostitutivo.descr"
                   );
        setColumnCaption("tipoAccettazione.descr", "Accettazione");
        setColumnCaption("tipoSostitutivo.descr", "TipoSostitutivo");
,
                        "tipoDocumento.bollettino",
                        "tipoAccettazione.descr","tipoSostitutivo.descr",
                        "provincia","ufficio","sportello","bobina", "progressivoBobina"                   */

    }

}
