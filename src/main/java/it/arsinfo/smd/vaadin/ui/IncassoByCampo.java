package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Button;

import it.arsinfo.smd.vaadin.model.SmdChangeHandler;

public class IncassoByCampo extends SmdChangeHandler {

    private final Button incassa;
    
    public IncassoByCampo() {
        incassa = new Button("Incassa usando il V Campo");
        incassa.addClickListener(click -> onChange());        
        setComponents(incassa);
    }
    
}
