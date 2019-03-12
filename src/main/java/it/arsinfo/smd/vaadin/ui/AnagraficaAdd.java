package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class AnagraficaAdd extends SmdAdd<Anagrafica> {



    public AnagraficaAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Anagrafica generate() {
        return new Anagrafica();
    }

}
