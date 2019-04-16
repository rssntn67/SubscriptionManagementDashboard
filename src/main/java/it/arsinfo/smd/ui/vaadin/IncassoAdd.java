package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Incasso;

public class IncassoAdd extends SmdAdd<Incasso> {


    public IncassoAdd(String caption) {
        super(caption);
    }

    @Override
    public Incasso generate() {
        return new Incasso();
    }

}
