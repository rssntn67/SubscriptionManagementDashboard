package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.vaadin.model.SmdBox;

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
