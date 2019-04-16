package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Nota;

public class NotaAdd extends SmdAdd<Nota> {

    public NotaAdd(String caption) {
        super(caption);
    }

    @Override
    public Nota generate() {
        return new Nota();
    }

}
