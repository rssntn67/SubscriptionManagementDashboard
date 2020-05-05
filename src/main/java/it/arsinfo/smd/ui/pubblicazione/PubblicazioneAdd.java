package it.arsinfo.smd.ui.pubblicazione;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class PubblicazioneAdd extends SmdAdd<Pubblicazione> {

    public PubblicazioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Pubblicazione generate() {
        return new Pubblicazione();
    }

}
