package it.arsinfo.smd.ui.distinta;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class DistintaVersamentoAdd extends SmdAdd<DistintaVersamento> {


    public DistintaVersamentoAdd(String caption) {
        super(caption);
    }

    @Override
    public DistintaVersamento generate() {
        return new DistintaVersamento();
    }

}
