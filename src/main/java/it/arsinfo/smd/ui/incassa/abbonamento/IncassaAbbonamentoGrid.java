package it.arsinfo.smd.ui.incassa.abbonamento;

public class IncassaAbbonamentoGrid extends IncassaAbbonamentoAbstractGrid {

    public IncassaAbbonamentoGrid(String gridname) {
        super(gridname);
        setColumns(                  
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "versamento.codeLine",
                "versamento.progressivo",
                "versamento.dataContabile",
                "versamento.importo",
                "versamento.incassato"
		);
        setColumnCaption("versamento.importo", "importo ver.");
        setColumnCaption("versamento.incassato", "incassato ver.");
        prependGridFooter();
    }

}
