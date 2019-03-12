package it.arsinfo.smd.vaadin.model;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdAdd<T extends SmdEntity>
        extends SmdChangeHandler {

    private Button add = new Button("Aggiungi", VaadinIcons.PLUS);

    public SmdAdd() {
        add.addClickListener(e -> onChange());
        setComponents(add);

    }

    public abstract T generate();
    
    public Button getAdd() {
        return add;
    }

}
