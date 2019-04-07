package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class OperazioneGrid extends SmdGrid<Operazione> {

    public OperazioneGrid(String gridName) {
        super(new Grid<>(Operazione.class), gridName);
        setColumns("pubblicazione.nome", "anno","mese", "stimato","definitivo");
        setColumnCaption("pubblicazione.nome", "Pubblicazione");
    }

}
