package it.arsinfo.smd.ui.campagna;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class CampagnaAdd extends SmdAdd<Campagna> {

    public CampagnaAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Campagna generate() {
        return new Campagna();
    }


}
