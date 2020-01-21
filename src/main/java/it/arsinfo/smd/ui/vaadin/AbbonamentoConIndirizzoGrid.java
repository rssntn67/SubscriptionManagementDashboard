package it.arsinfo.smd.ui.vaadin;

public class AbbonamentoConIndirizzoGrid extends AbbonamentoAbstractGrid {

    public AbbonamentoConIndirizzoGrid(String gridName) {
        super(gridName);
    }

	@Override
	public void setAbbonamentoGridColums() {
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
