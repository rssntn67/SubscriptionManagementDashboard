package it.arsinfo.smd.ui.storico;

import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class StoricoAdd extends SmdAdd<Storico> {

    public StoricoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Storico generate() {
        Storico storico= new Storico();
        return storico;
    }

}
