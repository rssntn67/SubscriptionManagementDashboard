package it.arsinfo.smd.ui.vaadin;

public class AbbonamentoGrid extends AbbonamentoAbstractGrid {

    public AbbonamentoGrid(String gridName) {
        super(gridName);
    }

	@Override
	public void setAbbonamentoGridColums() {
        setColumns("captionBrief",
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
