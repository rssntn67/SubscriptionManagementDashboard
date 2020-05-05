package it.arsinfo.smd.ui.nota;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class NotaAdd extends SmdAdd<Nota> {

    public NotaAdd(String caption) {
        super(caption);
    }

    @Override
    public Nota generate() {
        return new Nota();
    }

}
