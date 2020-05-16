package it.arsinfo.smd.ui.offerta;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class OfferteGrid extends SmdGrid<Offerta> {

    public OfferteGrid(String gridName) {
        super(new Grid<>(Offerta.class),gridName);
        setColumns(
                "operatore",
                "statoOperazioneIncasso",
                "committente.caption",
                "importo");
        setColumnCaption("committente.caption", "Committente");
   }
}
