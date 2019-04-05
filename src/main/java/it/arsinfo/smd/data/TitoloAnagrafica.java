package it.arsinfo.smd.data;

public enum TitoloAnagrafica {
        Nessuno(""),
	Signore("Egregio Sig."),
	Signora("Gentile Sig.ra"),
	Dottore("Egregio Dott."),
	Istituto("Spettabile"),
        Scuola("Spettabile"),
        Religioso("Padre"),
        Religiosa("Madre"),
        Parroco("Rev. Parroco"),
        Sacerdote("Rev."),
	Vescovo("S.E. Mons."),
	VescovoAusiliare("S.E. Mons."),
        VescovoMetropolita("S.E. Mons."),
        Arcivescovo("S.E. Mons."),
	Cardinale("S.Em. Card.");
    
    
    private String intestazione;
    
    private TitoloAnagrafica(String intestazione) {
        this.intestazione=intestazione;
    }

    public String getIntestazione() {
        return intestazione;
    }

    public void setIntestazione(String intestazione) {
        this.intestazione = intestazione;
    }
    
    
}