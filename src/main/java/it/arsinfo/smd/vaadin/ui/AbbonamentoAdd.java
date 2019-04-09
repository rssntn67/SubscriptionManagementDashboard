package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class AbbonamentoAdd extends SmdAdd<Abbonamento> {

    private Anagrafica primoIntestatario;
    
    public AbbonamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Abbonamento generate() {
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setIntestatario(primoIntestatario);
        return abbonamento;
    }

    public Anagrafica getPrimoIntestatario() {
        return primoIntestatario;
    }

    public void setPrimoIntestatario(Anagrafica primoIntestatario) {
        this.primoIntestatario = primoIntestatario;
    }

}
