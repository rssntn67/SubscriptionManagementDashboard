package it.arsinfo.smd.ui.ddt;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.DocumentoTrasporto;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class DocumentiTrasportoGrid extends SmdGrid<DocumentoTrasporto> {

    public DocumentiTrasportoGrid(String gridName) {
        super(new Grid<>(DocumentoTrasporto.class),gridName);
        setColumns(
        		"ddt",
                "operatore",
                "documentiTrasportoCumulati.anno",
                "statoOperazioneIncasso",
                "committente.caption",
                "importo");
        setColumnCaption("committente.caption", "Committente");
   }
}
