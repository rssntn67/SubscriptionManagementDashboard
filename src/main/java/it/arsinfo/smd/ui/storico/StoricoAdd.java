package it.arsinfo.smd.ui.storico;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class StoricoAdd extends SmdAdd<Storico> {

    private Anagrafica intestatario;
    private Pubblicazione pubblicazione;

    public StoricoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Storico generate() {
        Storico storico= new Storico();
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        return storico;
    }

    public void setIntestatario(Anagrafica intestatario) {
        this.intestatario = intestatario;
    }

	public void setPubblicazione(Pubblicazione pubblicazione) {
		this.pubblicazione = pubblicazione;
	}

}
