package it.arsinfo.smd.ui.spesaspedizione;

import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class SpesaSpedizioneAdd extends SmdAdd<SpesaSpedizione> {

    public SpesaSpedizioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public SpesaSpedizione generate() {
        return new SpesaSpedizione();
    }

}
