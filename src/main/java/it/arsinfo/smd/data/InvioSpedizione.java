package it.arsinfo.smd.data;

public enum InvioSpedizione {
    AdpSedeCorriere3gg("Spedizione da sede con corriere consegna 3gg"),
    AdpSedeCorriere24hh("Spedizione da sede con corriere consegna 24/48h"),
    AdpSede("Spedizione da sede con posta ordinaria"),
    AdpSedeNoSpese("Spedizione da sede con posta ordinaria senza oneri per il ricevente"),
    Spedizioniere("Spedizione da spedizioniere");
    
    private final String descr;
    
    InvioSpedizione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }
}