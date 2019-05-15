package it.arsinfo.smd.data;

public enum TitoloAnagrafica {
        Nessuno(""),
        Cattedrale("Spett.le"),
        Diocesi("Spett.le"),
        Parrocchia("Parrocchia"),
	Istituto("Spett.le"),
        Scuola("Spett.le"),
        Signore("Egr. Sig."),
        Signora("Gent. Sig.ra"),
        Dottore("Egregio Dott."),
        Religioso("Rev. Padre"),
        Religiosa("Rev. Madre"),
        Superiore("Rev. Padre Superiore"),
        Superiora("Rev. Madre Superiora"),
        Don("Don"),
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