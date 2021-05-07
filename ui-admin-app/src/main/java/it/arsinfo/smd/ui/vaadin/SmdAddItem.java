package it.arsinfo.smd.ui.vaadin;

import com.vaadin.icons.VaadinIcons;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;
import it.arsinfo.smd.service.api.SmdServiceItem;

public class SmdAddItem<I extends SmdEntity, T extends SmdEntityItems<I>>
        extends SmdButton {

    private T entity;
    private final SmdServiceItem<T,I> service;
    public SmdAddItem(String caption, SmdServiceItem<T,I> service) {
        super(caption, VaadinIcons.PLUS);
        this.service=service;

    }
    
    public void set(T entity) {
        this.entity=entity;
    }
    public T get() {
        return entity;
    }

    public I generate() {
        return service.addItem(entity);
    }
    
    
}
