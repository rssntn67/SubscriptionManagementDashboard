package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.vaadin.model.SmdAdd;

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