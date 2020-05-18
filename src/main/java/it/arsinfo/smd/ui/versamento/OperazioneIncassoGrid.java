package it.arsinfo.smd.ui.versamento;

public class OperazioneIncassoGrid extends it.arsinfo.smd.ui.incassa.abbonamento.IncassaAbbonamentoAbstractGrid {

    public OperazioneIncassoGrid(String gridName) {
        super(gridName);
        setColumns(
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "abbonamento.intestatario.intestazione",
                "abbonamento.codeLine",
                "abbonamento.totale",
                "abbonamento.incassato",
                "abbonamento.residuo",
                "abbonamento.anno");
        setColumnCaption("abbonamento.intestatario.caption", "Intestatario");
        setColumnCaption("abbonamento.totale", "importo abb.");
        setColumnCaption("abbonamento.incassato", "incassato abb.");
        setColumnCaption("abbonamento.residuo", "residuo abb.");
        prependGridFooter();
   }
}
