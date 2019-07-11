package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.SpesaSpedizione;

public class SpesaSpedizioneAdd extends SmdAdd<SpesaSpedizione> {

    public SpesaSpedizioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public SpesaSpedizione generate() {
        return new SpesaSpedizione();
    }

}
