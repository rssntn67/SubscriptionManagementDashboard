package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Spedizione;

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
