package it.arsinfo.smd.ui.ddt;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.DocumentiTrasportoCumulati;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class DocumentiTrasportoCumulatiGrid extends SmdGrid<DocumentiTrasportoCumulati>{

	public DocumentiTrasportoCumulatiGrid(String gridName) {
		super(new Grid<>(DocumentiTrasportoCumulati.class), gridName);
        getGrid().addColumn("anno.anno").setCaption("Anno");
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
		getGrid().setColumnOrder("anno.anno","importo");
	}
	

}
