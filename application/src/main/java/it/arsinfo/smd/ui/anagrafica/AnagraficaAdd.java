package it.arsinfo.smd.ui.anagrafica;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

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
