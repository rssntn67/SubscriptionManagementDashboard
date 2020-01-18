package it.arsinfo.smd.ui.vaadin;

public class OperazioneIncassoAbbonamentoGrid extends OperazioneIncassoVersamentoGrid {

    public OperazioneIncassoAbbonamentoGrid(String gridName) {
        super(gridName);
        setColumns(
                "operatore",
                "statoOperazioneIncasso",
                "importo",
                "abbonamento.intestatario.captionBrief",
                "abbonamento.codeLine",
                "abbonamento.totale",
                "abbonamento.incassato",
                "abbonamento.residuo",
                "abbonamento.anno");
        setColumnCaption("intestatario.captionBrief", "Intestatario");
        setColumnCaption("abbonamento.totale", "importo abb.");
        setColumnCaption("abbonamento.incassato", "incassato abb.");
        setColumnCaption("abbonamento.residuo", "residuo abb.");
   }
}
