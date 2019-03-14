package it.arsinfo.smd.vaadin.ui;

import com.vaadin.annotations.Title;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.vaadin.model.SmdGrid;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

public class PubblicazioneGrid extends SmdGrid<Pubblicazione> {

    public PubblicazioneGrid() {
        super(new Grid<>(Pubblicazione.class));
        setColumns("id", "nome", "tipo", "costoUnitario", "costoScontato",
                   "primaPubblicazione");

    }

}
