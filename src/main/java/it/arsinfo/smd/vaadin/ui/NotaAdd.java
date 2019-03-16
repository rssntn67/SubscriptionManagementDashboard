package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class NotaAdd extends SmdAdd<Nota> {

    private final Anagrafica primoIntestatario;

    public NotaAdd(String caption, Anagrafica primoIntestatario) {
        super(caption);
        this.primoIntestatario=primoIntestatario;
    }

    @Override
    public Nota generate() {
        return new Nota(primoIntestatario);
    }

}
