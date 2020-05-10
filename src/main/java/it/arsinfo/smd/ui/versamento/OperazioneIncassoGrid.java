package it.arsinfo.smd.ui.versamento;

public class OperazioneIncassoGrid extends it.arsinfo.smd.ui.incassa.OperazioneIncassoGrid {

    public OperazioneIncassoGrid(String gridName) {
        super(gridName);
        setColumns(
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "abbonamento.intestatario.caption",
                "abbonamento.codeLine",
                "abbonamento.totale",
                "abbonamento.incassato",
                "abbonamento.residuo",
                "abbonamento.anno");
        setColumnCaption("intestatario.caption", "Intestatario");
        setColumnCaption("abbonamento.totale", "importo abb.");
        setColumnCaption("abbonamento.incassato", "incassato abb.");
        setColumnCaption("abbonamento.residuo", "residuo abb.");
   }
}
