package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public class SpesaSpedizioneAdd extends SmdAdd<SpesaSpedizione> {

    private Pubblicazione pubblicazione;

    public SpesaSpedizioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public SpesaSpedizione generate() {
        SpesaSpedizione ec = new SpesaSpedizione();
        ec.setPubblicazione(pubblicazione);
        return ec;
    }

    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }

    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

}
