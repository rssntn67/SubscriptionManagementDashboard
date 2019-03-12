package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class NotaAdd extends SmdAdd<Nota> {

    public NotaAdd(String caption) {
        super(caption);
    }

    @Override
    public Nota generate() {
        return new Nota();
    }

}
