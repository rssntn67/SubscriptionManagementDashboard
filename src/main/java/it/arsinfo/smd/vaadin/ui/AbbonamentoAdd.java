package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class AbbonamentoAdd extends SmdAdd<Abbonamento> {

    private final Anagrafica primoIntestatario;
    
    public AbbonamentoAdd(String caption, Anagrafica primoIntestatario) {
        super(caption);
        this.primoIntestatario=primoIntestatario;
    }
    
    @Override
    public Abbonamento generate() {
        return new Abbonamento(primoIntestatario);
    }

}
