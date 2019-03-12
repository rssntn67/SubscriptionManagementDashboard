package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class CampagnaAdd extends SmdAdd<Campagna> {



    public CampagnaAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Campagna generate() {
        return new Campagna();
    }


}
