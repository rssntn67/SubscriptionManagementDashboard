package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Anagrafica;

public class AnagraficaAdd extends SmdAdd<Anagrafica> {

    public AnagraficaAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Anagrafica generate() {
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setNome("Nome");
        anagrafica.setDenominazione("Denominazione");
        anagrafica.setCodeLineBase(Anagrafica.generaCodeLineBase());
        return anagrafica;
    }

}
