package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class AbbonamentoAdd extends SmdAdd<Abbonamento> {



    public AbbonamentoAdd() {
        super();
    }
    
    @Override
    public Abbonamento generate() {
        return new Abbonamento();
    }

}
