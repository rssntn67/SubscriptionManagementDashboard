package it.arsinfo.smd.ui.incassa.abbonamento;

import it.arsinfo.smd.ui.EuroConverter;

public class IncassaAbbonamentoGrid extends IncassaAbbonamentoAbstractGrid {

    public IncassaAbbonamentoGrid(String gridname) {
        super(gridname);
        getGrid().addColumn("operatore");
        getGrid().addColumn("statoOperazioneIncasso");
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("versamento.codeLine");
        getGrid().addColumn("versamento.progressivo");
        getGrid().addColumn("versamento.dataContabile");
        getGrid().addColumn("versamento.importo",EuroConverter.getEuroRenderer()).setCaption("importo ver.");
        getGrid().addColumn("versamento.incassato",EuroConverter.getEuroRenderer()).setCaption("incassato ver.");
        
        getGrid().setColumnOrder(                  
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "versamento.codeLine",
                "versamento.progressivo",
                "versamento.dataContabile",
                "versamento.importo",
                "versamento.incassato"
		);
        prependGridFooter();
    }

}
