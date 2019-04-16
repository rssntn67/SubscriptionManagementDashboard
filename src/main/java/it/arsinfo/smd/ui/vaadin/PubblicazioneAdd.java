package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Pubblicazione;

public class PubblicazioneAdd extends SmdAdd<Pubblicazione> {

    public PubblicazioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Pubblicazione generate() {
        return new Pubblicazione();
    }

}
