package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Campagna;

public class CampagnaAdd extends SmdAdd<Campagna> {

    public CampagnaAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Campagna generate() {
        return new Campagna();
    }


}
