package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;

public class VersamentoAdd extends SmdAdd<Versamento> {

    private Incasso incasso;

    public VersamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Versamento generate() {
        return new Versamento(incasso);
    }

    public Incasso getIncasso() {
        return incasso;
    }

    public void setIncasso(Incasso incasso) {
        this.incasso = incasso;
    }

}
