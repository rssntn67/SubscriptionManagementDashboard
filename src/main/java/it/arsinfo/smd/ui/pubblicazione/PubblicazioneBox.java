package it.arsinfo.smd.ui.pubblicazione;

import java.util.List;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdCheckBoxArray;

public class PubblicazioneBox extends SmdCheckBoxArray<Pubblicazione> {

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
