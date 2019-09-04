package it.arsinfo.smd.data;


public enum TitoloAnagrafica {
        Nessuno(""),
        Diocesi("Spett.le"),
        Istituto("Spett.le"),
        Cattedrale("Spett.le"),
        Ditta("Spett.le"),
        CasaDiRiposo("Spett.le"),
        Diacono("Rev. Diac."),
        Parrocchia("Parrocchia"),
        Insegnante("Gent. Ins."),
        Scuola("Spett.le"),
        Famiglia("Gent.ma Famiglia"),
        Egregio("Egr. Sig."),
        Egregi("Egr. Sigg."),
        Gentilissima("Gent. Sig.ra"),
        Gentilissime("Gent.me Sigg."),
        Signorina("Gent. Sig.na"),
        Dottore("Egr. Dott."),
        Dottoressa("Gent. Dott.ssa"),
        Ingegnere("Egr. Ing."),
        Religioso("Rev. Padre"),
        Religiosa("Rev. Madre"),
        Superiore("R. P. Superiore"),
        Superiora("R. M. Superiora"),
        Suor("Rev. Suora"),
        Suore("Rev. Suore"),
        Don("Don"),
        Parroco("Rev. Parroco"),
        Sacerdote("Rev."),
        Monsignore("M. R. Mons."),
        Rettore("M. R. Rettore"),
	Vescovo("S. E. Mons."),
	VescovoAusiliare("S. E. Mons."),
        VescovoMetropolita("S. E. Mons."),
        Arcivescovo("S. E. Mons."),
	Cardinale("S. Em. Card.");
    
    
    private String intestazione;
    
    public static TitoloAnagrafica getByIntestazione(String intestazione) {
        for (TitoloAnagrafica ta: TitoloAnagrafica.values()) {
            if (ta.getIntestazione().equals(intestazione)) {
                return ta;
            }
        }
        return TitoloAnagrafica.Nessuno;
    }

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