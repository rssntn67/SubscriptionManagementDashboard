package it.arsinfo.smd.ui.campagna;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.OperazioneSospendi;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class OperazioneSospendiGrid extends SmdGrid<OperazioneSospendi> {

    public OperazioneSospendiGrid(String gridName) {
        super(new Grid<>(OperazioneSospendi.class),gridName);
        setColumns(
        		"pubblicazione.nome",
        		"campagna.anno.anno",
        		"meseSpedizione.nomeBreve",
        		"data",
        		"operatore");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
        setColumnCaption("campagna.anno.anno", "Anno");
        setColumnCaption("meseSpedizione.nomeBreve", "Mese");
	}

}
