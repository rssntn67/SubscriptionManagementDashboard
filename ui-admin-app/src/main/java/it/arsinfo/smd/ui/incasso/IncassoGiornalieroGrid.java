package it.arsinfo.smd.ui.incasso;

import com.vaadin.ui.Grid;
import it.arsinfo.smd.service.dto.IncassoGiornaliero;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class IncassoGiornalieroGrid extends SmdGrid<IncassoGiornaliero> {

    public IncassoGiornalieroGrid(String gridName) {
        super(new Grid<>(IncassoGiornaliero.class),gridName);
        getGrid().addColumn("dataIncasso");
        getGrid().addColumn("cassa");
        getGrid().addColumn("ccp.ccp");
        getGrid().addColumn("messaggio",EuroConverter.getEuroRenderer());
        getGrid().addColumn("blocchetti",EuroConverter.getEuroRenderer());
        getGrid().addColumn("manifesti",EuroConverter.getEuroRenderer());
        getGrid().addColumn("lodare",EuroConverter.getEuroRenderer());
        getGrid().addColumn("nonAssociati",EuroConverter.getEuroRenderer());
        getGrid().addColumn("incassato",EuroConverter.getEuroRenderer());
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("residuo",EuroConverter.getEuroRenderer());

	}


}
