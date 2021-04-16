package it.arsinfo.smd.ui.versamento;

import it.arsinfo.smd.ui.EuroConverter;

public class OperazioneIncassoGrid extends it.arsinfo.smd.ui.incassa.abbonamento.IncassaAbbonamentoAbstractGrid {

    public OperazioneIncassoGrid(String gridName) {
        super(gridName);
        getGrid().addColumn("operatore");
        getGrid().addColumn("statoOperazioneIncasso").setCaption("Operazione");
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("abbonamento.intestatario.intestazione").setCaption("Intestatario");
        getGrid().addColumn("abbonamento.codeLine");
        getGrid().addColumn("abbonamento.totale",   EuroConverter.getEuroRenderer()).setCaption("Importo su Abb.to");
        getGrid().addColumn("abbonamento.incassato",EuroConverter.getEuroRenderer()).setCaption("Incasso su Abb.to");
        getGrid().addColumn("abbonamento.residuo",  EuroConverter.getEuroRenderer()).setCaption("Residuo su Abb.to");
        getGrid().addColumn("abbonamento.anno").setCaption("Anno Abb.to");

        getGrid().setColumnOrder(
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "abbonamento.intestatario.intestazione",
                "abbonamento.codeLine",
                "abbonamento.totale",
                "abbonamento.incassato",
                "abbonamento.residuo",
                "abbonamento.anno"
            );

        prependGridFooter();
   }
}
