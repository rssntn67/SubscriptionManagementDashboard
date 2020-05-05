package it.arsinfo.smd.ui.pubblicazione;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class PubblicazioneGrid extends SmdGrid<Pubblicazione> {

    public PubblicazioneGrid(String gridname) {
        super(new Grid<>(Pubblicazione.class),gridname);
        setColumns("nome", "autore","decodeAttivo","editore","tipo.descrizione", "costoUnitario", "abbonamento",
                   "pubblicato");
        setColumnCaption("decodeAttivo", "Attiva");

    }
}
