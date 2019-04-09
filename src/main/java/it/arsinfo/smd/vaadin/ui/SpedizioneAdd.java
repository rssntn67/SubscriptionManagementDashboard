package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class SpedizioneAdd extends SmdAdd<Spedizione> {

    private Abbonamento abbonamento;

    public SpedizioneAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Spedizione generate() {
        Spedizione spedizione = new Spedizione();
        spedizione.setAbbonamento(abbonamento);
        return spedizione;
    }

    public Abbonamento getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(Abbonamento intestatario) {
        this.abbonamento = intestatario;
    }

}
