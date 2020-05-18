package it.arsinfo.smd.ui.offerta;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class OfferteCumulateGrid extends SmdGrid<OfferteCumulate>{

	public OfferteCumulateGrid(String gridName) {
		super(new Grid<>(OfferteCumulate.class), gridName);
        setColumns("anno.annoAsString","importo");
     setColumnCaption("anno.annoAsString", "Anno");

	}
	

}
