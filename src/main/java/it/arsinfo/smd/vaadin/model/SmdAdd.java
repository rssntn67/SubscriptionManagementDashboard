package it.arsinfo.smd.vaadin.model;

import com.vaadin.icons.VaadinIcons;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdAdd<T extends SmdEntity>
        extends SmdButton {

    public SmdAdd(String caption) {
        super(caption, VaadinIcons.PLUS);

    }

    public abstract T generate();
    
}
