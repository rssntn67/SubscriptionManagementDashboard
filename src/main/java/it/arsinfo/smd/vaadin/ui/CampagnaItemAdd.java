package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class CampagnaItemAdd extends SmdAdd<CampagnaItem> {

    private Campagna campagna;

    public CampagnaItemAdd(String caption) {
        super(caption);
    }
    
    @Override
    public CampagnaItem generate() {
        return new CampagnaItem(campagna);
    }

    public Campagna getCampagna() {
        return campagna;
    }

    public void setCampagna(Campagna campagna) {
        this.campagna = campagna;
    }

}
