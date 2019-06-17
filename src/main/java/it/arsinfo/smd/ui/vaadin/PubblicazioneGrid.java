package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Pubblicazione;

public class PubblicazioneGrid extends SmdGrid<Pubblicazione> {

    public PubblicazioneGrid(String gridname) {
        super(new Grid<>(Pubblicazione.class),gridname);
        setColumns("nome", "autore","decodeAttivo","editore","tipo.descrizione", "costoUnitario", "abbonamentoItalia",
                   "pubblicato");
        setColumnCaption("decodeAttivo", "Attiva");

    }

}
