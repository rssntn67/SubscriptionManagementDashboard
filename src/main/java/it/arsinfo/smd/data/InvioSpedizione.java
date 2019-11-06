package it.arsinfo.smd.data;

public enum InvioSpedizione {
    AdpSedeCorriere3gg("Spedizione da sede con corriere consegna 3gg"),
    AdpSedeCorriere24hh("Spedizione da sede con corriere consegna 24/48h"),
    AdpSede("Spedizione da sede con posta ordinaria"),
    Spedizioniere("Spedizione da spedizioniere");
    
    String descr;
    
    private InvioSpedizione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}