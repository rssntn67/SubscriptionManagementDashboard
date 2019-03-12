package it.arsinfo.smd.vaadin.model;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdAdd<T extends SmdEntity>
        extends SmdChangeHandler {

    private final Button add;

    public SmdAdd(String caption) {
        add = new Button(caption, VaadinIcons.PLUS);
        add.addClickListener(e -> onChange());
        setComponents(add);

    }

    public abstract T generate();
    
    public Button getAdd() {
        return add;
    }

}
