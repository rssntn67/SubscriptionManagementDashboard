package it.arsinfo.smd.ui.vaadin;

public class AbbonamentoConIndirizzoGrid extends AbbonamentoGrid {

    public AbbonamentoConIndirizzoGrid(String gridName) {
        super(gridName);
        setColumns("captionBrief",
        		"sottoIntestazione",
        		"indirizzo",
        		"citta",
        		"cap",
        		"provincia",
        		"paese",
                "importo",
                "spese",
                "speseEstero",
                "speseEstrattoConto",
                "pregresso",
                "totale",
                "incassato",
                "residuo",
                "anno");
        setColumnCaption("captionBrief", "Intestatario");
    }

}
