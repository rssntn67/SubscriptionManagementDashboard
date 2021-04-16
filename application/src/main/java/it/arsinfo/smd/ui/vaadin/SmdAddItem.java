package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;

public abstract class SmdAddItem<I extends SmdEntity, T extends SmdEntityItems<I>>
        extends SmdAdd<I> {

    public SmdAddItem(String caption) {
        super(caption);

    }
    
    public abstract void set(T t);
    
    
}
