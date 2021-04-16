package it.arsinfo.smd.ui.offerta;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class OfferteCumulateGrid extends SmdGrid<OfferteCumulate>{

	public OfferteCumulateGrid(String gridName) {
		super(new Grid<>(OfferteCumulate.class), gridName);
        getGrid().addColumn("anno.anno").setCaption("Anno");
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
	}
	

}
