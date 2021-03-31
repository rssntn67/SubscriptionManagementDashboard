package it.arsinfo.smd.data;


public enum TitoloAnagrafica {
        Nessuno("",false),
        Spettabile("Spett.le", false),
        Adp("Spett.le Apostolato della Preghiera",false),
        Diocesi("Spett.le",false),
        Istituto("Spett.le",false),
        Cattedrale("Spett.le",false),
        Ditta("Spett.le",false),
        CasaDiRiposo("Spett.le",false),
        Diacono("Rev. Diac."),
        Parrocchia("Parrocchia",false),
        Insegnante("Gent. Ins."),
        Scuola("Spett.le",false),
        Famiglia("Gent.ma Famiglia",false),
        Egregio("Egr. Sig."),
        Egregi("Egr. Sigg."),
        Gentilissima("Gent. Sig.ra"),
        Gentilissime("Gent.me Sigg.", false),
        Signorina("Gent. Sig.na"),
        Dottore("Egr. Dott."),
        Dottoressa("Gent. Dott.ssa"),
        Ingegnere("Egr. Ing."),
        Religioso("Rev. Padre"),
        Religiosa("Rev. Madre"),
        Superiore("R. P. Superiore", false),
        Superiora("R. M. Superiora",false),
        Suor("Rev. Suora"),
        Suore("Rev. Suore",false),
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
    
    
    private final String intestazione;
    private final boolean persona;
    
    public static TitoloAnagrafica getByIntestazione(String intestazione) {
        for (TitoloAnagrafica ta: TitoloAnagrafica.values()) {
            if (ta.getIntestazione().equals(intestazione)) {
                return ta;
            }
        }
        return TitoloAnagrafica.Nessuno;
    }

    TitoloAnagrafica(String intestazione) {
        this.intestazione=intestazione;
        this.persona = true;
    }

    TitoloAnagrafica(String intestazione,boolean persona) {
        this.intestazione=intestazione;
        this.persona = persona;
    }

    public String getIntestazione() {
        return intestazione;
    }
    public boolean isPersona() {
        return persona;
    }

}