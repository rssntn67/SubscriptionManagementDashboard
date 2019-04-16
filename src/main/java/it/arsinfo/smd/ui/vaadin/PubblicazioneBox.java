package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import it.arsinfo.smd.entity.Pubblicazione;

public class PubblicazioneBox extends SmdBox<Pubblicazione> {

    public PubblicazioneBox(List<Pubblicazione> pubblicazioni) {
        super(pubblicazioni);
    }

    @Override
    public String getBoxCaption(Pubblicazione t) {
        return t.getNome();
    }

    @Override
    public boolean getReadOnly(Pubblicazione t, boolean persisted) {
        return false;
    }

    
}
