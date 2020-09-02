package it.arsinfo.smd.ui.campagna;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.OperazioneCampagna;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class OperazioneCampagnaGrid extends SmdGrid<OperazioneCampagna> {

    public OperazioneCampagnaGrid(String gridName) {
        super(new Grid<>(OperazioneCampagna.class),gridName);
        setColumns(
        		"stato",
        		"data",
        		"operatore");
	}

}
