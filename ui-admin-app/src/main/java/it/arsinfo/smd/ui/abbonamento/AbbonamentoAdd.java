package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class AbbonamentoAdd extends SmdAdd<Abbonamento> {
    
    public AbbonamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Abbonamento generate() {
        return new Abbonamento();
    }


}
