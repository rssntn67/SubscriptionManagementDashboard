package it.arsinfo.smd.ui.incasso;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class IncassoAdd extends SmdAdd<DistintaVersamento> {


    public IncassoAdd(String caption) {
        super(caption);
    }

    @Override
    public DistintaVersamento generate() {
        return new DistintaVersamento();
    }

}
