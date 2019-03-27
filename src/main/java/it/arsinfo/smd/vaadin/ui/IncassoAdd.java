package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class IncassoAdd extends SmdAdd<Incasso> {


    public IncassoAdd(String caption) {
        super(caption);
    }

    @Override
    public Incasso generate() {
        return new Incasso();
    }

}
