package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class StoricoAdd extends SmdAdd<Storico> {

    private Anagrafica intestatario;

    public StoricoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Storico generate() {
        Storico storico= new Storico();
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica intestatario) {
        this.intestatario = intestatario;
    }

}
