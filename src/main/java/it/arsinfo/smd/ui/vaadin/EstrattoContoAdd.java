package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;

public class EstrattoContoAdd extends SmdAdd<EstrattoConto> {

    private Abbonamento abbonamento;

    public EstrattoContoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public EstrattoConto generate() {
        EstrattoConto spedizione = new EstrattoConto();
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
