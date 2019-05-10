package it.arsinfo.smd.data;

public enum TitoloAnagrafica {
        Nessuno(""),
	Signore("Egregio Sig."),
	Signora("Gentile Sig.ra"),
	Dottore("Egregio Dott."),
	Istituto("Spett.le Istituto"),
        Scuola("Spett.le Scuola"),
        Religioso("Rev. Padre"),
        Religiosa("Rev. Madre"),
        Superiore("Rev. Padre Superiore"),
        Superiora("Rev. Madre Superiora"),
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