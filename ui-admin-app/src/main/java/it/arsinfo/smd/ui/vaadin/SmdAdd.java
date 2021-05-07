package it.arsinfo.smd.ui.vaadin;

import com.vaadin.icons.VaadinIcons;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.service.api.SmdServiceBase;

public class SmdAdd<T extends SmdEntity>
        extends SmdButton {

    private final SmdServiceBase<T> service;
    public SmdAdd(String caption, SmdServiceBase<T> service) {
        super(caption, VaadinIcons.PLUS);
        this.service=service;
    }

    public T generate() {
           return service.add();
    }
    
}
