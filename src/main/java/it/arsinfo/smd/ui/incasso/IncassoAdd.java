package it.arsinfo.smd.ui.incasso;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class IncassoAdd extends SmdAdd<Incasso> {


    public IncassoAdd(String caption) {
        super(caption);
    }

    @Override
    public Incasso generate() {
        return new Incasso();
    }

}
