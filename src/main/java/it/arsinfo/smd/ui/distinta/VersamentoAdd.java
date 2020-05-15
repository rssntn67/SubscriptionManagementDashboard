package it.arsinfo.smd.ui.distinta;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class VersamentoAdd extends SmdAdd<Versamento> {

    private DistintaVersamento incasso;

    public VersamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Versamento generate() {
        return new Versamento(incasso);
    }

    public DistintaVersamento getIncasso() {
        return incasso;
    }

    public void setIncasso(DistintaVersamento incasso) {
        this.incasso = incasso;
    }

}
